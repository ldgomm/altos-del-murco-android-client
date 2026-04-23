package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.ClientProfileRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfile
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfileDocument
import com.premierdarkcoffee.tourism.altosdelmurco.util.database.awaitResult
import com.premierdarkcoffee.tourism.altosdelmurco.util.constant.FirestoreCollections
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreClientProfileRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : ClientProfileRepositoriable {

    private val collection = firestore.collection(FirestoreCollections.CLIENTS)

    override suspend fun fetchProfile(uid: String): ClientProfile? {
        val cleanUid = uid.trim()
        if (cleanUid.isEmpty()) return null

        val snapshot = collection.document(cleanUid).get().awaitResult()
        if (!snapshot.exists()) return null

        val document = snapshot.toObject(ClientProfileDocument::class.java) ?: return null
        return document.toDomain()
    }

    override suspend fun saveProfile(profile: ClientProfile) {
        collection
            .document(profile.id)
            .set(ClientProfileDocument(profile), SetOptions.merge())
            .awaitResult()
    }

    override suspend fun deleteProfile(uid: String) {
        val cleanUid = uid.trim()
        if (cleanUid.isEmpty()) return
        collection.document(cleanUid).delete().awaitResult()
    }
}
