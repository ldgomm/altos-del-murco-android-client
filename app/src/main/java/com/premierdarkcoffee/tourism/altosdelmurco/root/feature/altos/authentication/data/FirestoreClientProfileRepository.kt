package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.ClientProfileRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfile
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfileDocument
import com.premierdarkcoffee.tourism.altosdelmurco.util.constant.FirestoreCollections
import com.premierdarkcoffee.tourism.altosdelmurco.util.database.awaitResult
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreClientProfileRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : ClientProfileRepositoriable {

    private val collection = firestore.collection(FirestoreCollections.CLIENTS)

    companion object {
        private const val TAG = "AltosProfileRepo"
    }

    override suspend fun fetchProfile(uid: String): ClientProfile? {
        val cleanUid = uid.trim()
        if (cleanUid.isEmpty()) {
            Log.d(TAG, "fetchProfile -> empty uid")
            return null
        }

        Log.d(TAG, "fetchProfile -> requesting clients/$cleanUid")

        val snapshot = collection.document(cleanUid).get().awaitResult()

        Log.d(
            TAG,
            "fetchProfile -> snapshot exists=${snapshot.exists()}, id=${snapshot.id}, keys=${snapshot.data?.keys?.sorted()}"
        )

        if (!snapshot.exists()) {
            Log.d(TAG, "fetchProfile -> document does not exist for uid=$cleanUid")
            return null
        }

        val profile = snapshot.toClientProfileOrNull()

        Log.d(
            TAG,
            "fetchProfile -> mapped profile null=${profile == null}, " +
                    "id=${profile?.id}, " +
                    "email=${profile?.email}, " +
                    "fullName='${profile?.fullName}', " +
                    "nationalId='${profile?.nationalId}', " +
                    "phone='${profile?.phoneNumber}', " +
                    "address='${profile?.address}', " +
                    "emergencyName='${profile?.emergencyContactName}', " +
                    "emergencyPhone='${profile?.emergencyContactPhone}', " +
                    "isProfileComplete=${profile?.isProfileComplete}, " +
                    "isComplete=${profile?.isComplete}"
        )

        return profile
    }

    override suspend fun saveProfile(profile: ClientProfile) {
        Log.d(
            TAG,
            "saveProfile -> writing clients/${profile.id.trim()} " +
                    "email=${profile.email}, " +
                    "fullName='${profile.fullName}', " +
                    "nationalId='${profile.nationalId}', " +
                    "phone='${profile.phoneNumber}', " +
                    "address='${profile.address}', " +
                    "emergencyName='${profile.emergencyContactName}', " +
                    "emergencyPhone='${profile.emergencyContactPhone}', " +
                    "isProfileComplete=${profile.isProfileComplete}, " +
                    "isComplete=${profile.isComplete}"
        )

        collection
            .document(profile.id.trim())
            .set(ClientProfileDocument(profile), SetOptions.merge())
            .awaitResult()

        Log.d(TAG, "saveProfile -> write success clients/${profile.id.trim()}")
    }

    override suspend fun deleteProfile(uid: String) {
        val cleanUid = uid.trim()
        if (cleanUid.isEmpty()) return
        collection.document(cleanUid).delete().awaitResult()
    }

    private fun DocumentSnapshot.toClientProfileOrNull(): ClientProfile? {
        return runCatching {
            ClientProfile(
                id = id.trim(),
                email = getString("email").orEmpty().trim(),
                appleUserIdentifier = getString("appleUserIdentifier").orEmpty().trim(),
                fullName = getString("fullName").orEmpty().trim(),
                nationalId = getString("nationalId").orEmpty().trim(),
                phoneNumber = getString("phoneNumber").orEmpty().trim(),
                birthday = getDateValue("birthday") ?: Date(0),
                address = getString("address").orEmpty().trim(),
                emergencyContactName = getString("emergencyContactName").orEmpty().trim(),
                emergencyContactPhone = getString("emergencyContactPhone").orEmpty().trim(),
                isProfileComplete = getBoolean("profileComplete") == true,
                createdAt = getDateValue("createdAt") ?: Date(),
                updatedAt = getDateValue("updatedAt") ?: Date(),
                profileCompletedAt = getDateValue("profileCompletedAt"),
                profileImageURL = getString("profileImageURL")?.trim()?.takeIf { it.isNotEmpty() },
                profileImagePath = getString("profileImagePath")?.trim()?.takeIf { it.isNotEmpty() },
            )
        }.onFailure { error ->
            Log.e(TAG, "toClientProfileOrNull -> mapping failed for docId=$id", error)
        }.getOrNull()
    }

    private fun DocumentSnapshot.getDateValue(field: String): Date? {
        return when (val value = get(field)) {
            is Timestamp -> value.toDate()
            is Date -> value
            else -> null
        }
    }
}