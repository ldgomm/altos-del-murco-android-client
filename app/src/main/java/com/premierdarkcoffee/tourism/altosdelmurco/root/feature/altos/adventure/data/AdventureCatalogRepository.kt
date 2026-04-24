package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureActivityCatalogItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureCatalogRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureCatalogSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureFeaturedPackage
import com.premierdarkcoffee.tourism.altosdelmurco.util.constant.FirestoreCollections
import com.premierdarkcoffee.tourism.altosdelmurco.util.database.awaitResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdventureCatalogRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : AdventureCatalogRepositoriable {

    override suspend fun fetchCatalog(): AdventureCatalogSnapshot {
        val activitiesSnapshot = firestore
            .collection(FirestoreCollections.ADVENTURE_ACTIVITIES)
            .get()
            .awaitResult()

        val packagesSnapshot = firestore
            .collection(FirestoreCollections.ADVENTURE_FEATURED_PACKAGES)
            .get()
            .awaitResult()

        return makeCatalogSnapshot(
            activitiesSnapshot = activitiesSnapshot,
            packagesSnapshot = packagesSnapshot,
        )
    }

    override fun observeCatalog(): Flow<AdventureCatalogSnapshot> = callbackFlow {
        var latestActivities: QuerySnapshot? = null
        var latestPackages: QuerySnapshot? = null

        fun emitIfReady() {
            val activities = latestActivities ?: return
            val packages = latestPackages ?: return
            runCatching {
                makeCatalogSnapshot(
                    activitiesSnapshot = activities,
                    packagesSnapshot = packages,
                )
            }.onSuccess { snapshot ->
                trySend(snapshot).isSuccess
            }.onFailure { error ->
                close(error)
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
        activitiesSnapshot: QuerySnapshot,
        packagesSnapshot: QuerySnapshot,
    ): AdventureCatalogSnapshot {
        val activities: List<AdventureActivityCatalogItem> = activitiesSnapshot.documents
            .mapNotNull { document ->
                document.toObject(AdventureActivityCatalogDto::class.java)?.toDomain()
            }
            .sortedWith(compareBy<AdventureActivityCatalogItem> { it.sortOrder }.thenBy { it.title })

        val activitiesByType = activities.associateBy { it.activityType }

        val packages: List<AdventureFeaturedPackage> = packagesSnapshot.documents
            .mapNotNull { document ->
                val dto = document.toObject(AdventureFeaturedPackageDto::class.java)
                    ?: return@mapNotNull null
                if (!dto.isActive) return@mapNotNull null

                val packageModel = dto.toDomain() ?: return@mapNotNull null
                val allItemsActive = packageModel.items.all { item ->
                    activitiesByType[item.activity]?.isActive == true
                }
                if (!allItemsActive) return@mapNotNull null

                packageModel
            }
            .sortedWith(compareBy<AdventureFeaturedPackage> { it.sortOrder }.thenBy { it.title })

        return AdventureCatalogSnapshot(
            activities = activities,
            featuredPackages = packages,
        )
    }
}