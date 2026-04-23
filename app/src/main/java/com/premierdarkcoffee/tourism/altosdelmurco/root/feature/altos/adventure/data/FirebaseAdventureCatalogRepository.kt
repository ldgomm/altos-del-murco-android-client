package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureCatalogRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureCatalogSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureFeaturedPackage
import com.premierdarkcoffee.tourism.altosdelmurco.util.constant.FirestoreCollections
import com.premierdarkcoffee.tourism.altosdelmurco.util.database.awaitResult
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@Singleton
class FirebaseAdventureCatalogRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : AdventureCatalogRepository {

    override suspend fun fetchCatalog(): AdventureCatalogSnapshot {
        val activitiesSnapshot =
            firestore.collection(FirestoreCollections.ADVENTURE_ACTIVITIES).get().awaitResult()
        val packagesSnapshot =
            firestore.collection(FirestoreCollections.ADVENTURE_FEATURED_PACKAGES).get()
                .awaitResult()
        return makeCatalogSnapshot(activitiesSnapshot, packagesSnapshot)
    }

    override fun observeCatalog(): Flow<AdventureCatalogSnapshot> = callbackFlow {
        var latestActivities: com.google.firebase.firestore.QuerySnapshot? = null
        var latestPackages: com.google.firebase.firestore.QuerySnapshot? = null

        fun emitIfReady() {
            val activities = latestActivities
            val packages = latestPackages
            if (activities != null && packages != null) {
                trySend(makeCatalogSnapshot(activities, packages)).isSuccess
            }
        }

        val activitiesRegistration: ListenerRegistration = firestore
            .collection(FirestoreCollections.ADVENTURE_ACTIVITIES)
            .addSnapshotListener { snapshot, error ->
                when {
                    error != null -> close(error)
                    snapshot != null -> {
                        latestActivities = snapshot
                        emitIfReady()
                    }
                }
            }

        val packagesRegistration: ListenerRegistration = firestore
            .collection(FirestoreCollections.ADVENTURE_FEATURED_PACKAGES)
            .addSnapshotListener { snapshot, error ->
                when {
                    error != null -> close(error)
                    snapshot != null -> {
                        latestPackages = snapshot
                        emitIfReady()
                    }
                }
            }

        awaitClose {
            activitiesRegistration.remove()
            packagesRegistration.remove()
        }
    }

    private fun makeCatalogSnapshot(
        activitiesSnapshot: com.google.firebase.firestore.QuerySnapshot,
        packagesSnapshot: com.google.firebase.firestore.QuerySnapshot,
    ): AdventureCatalogSnapshot {
        val activities = activitiesSnapshot.documents.mapNotNull { doc ->
            doc.toObject(AdventureActivityCatalogDto::class.java)?.toDomain()
        }

        val activitiesByType = activities.associateBy { it.activityType }

        val packages: List<AdventureFeaturedPackage> =
            packagesSnapshot.documents.mapNotNull { doc ->
                val dto =
                    doc.toObject(AdventureFeaturedPackageDto::class.java) ?: return@mapNotNull null
                if (!dto.isActive) return@mapNotNull null
                val packageModel = dto.toDomain() ?: return@mapNotNull null
                val allItemsActive =
                    packageModel.items.all { item -> activitiesByType[item.activity]?.isActive == true }
                if (!allItemsActive) return@mapNotNull null
                packageModel
            }

        return AdventureCatalogSnapshot(
            activities = activities.sortedWith(compareBy({ it.sortOrder }, { it.title })),
            featuredPackages = packages.sortedWith(compareBy({ it.sortOrder }, { it.title })),
        )
    }
}
