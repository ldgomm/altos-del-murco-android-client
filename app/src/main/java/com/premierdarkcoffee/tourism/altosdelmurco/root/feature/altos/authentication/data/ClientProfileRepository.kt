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
class ClientProfileRepository @Inject constructor(
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

        val snapshot = collection.document(cleanUid).get().awaitResult()

        if (!snapshot.exists()) {
            Log.d(TAG, "fetchProfile -> no profile for uid=${cleanUid.safeTail()}")
            return null
        }

        val profile = snapshot.toClientProfileOrNull()

        Log.d(
            TAG,
            "fetchProfile -> mapped=${profile != null}, uid=${cleanUid.safeTail()}, " +
                    "complete=${profile?.isProfileComplete == true}"
        )

        return profile
    }

    override suspend fun saveProfile(profile: ClientProfile) {
        val cleanUid = profile.id.trim()
        require(cleanUid.isNotEmpty()) { "Profile id is required." }

        Log.d(
            TAG,
            "saveProfile -> uid=${cleanUid.safeTail()}, complete=${profile.isProfileComplete}"
        )

        collection
            .document(cleanUid)
            .set(ClientProfileDocument(profile), SetOptions.merge())
            .awaitResult()

        Log.d(TAG, "saveProfile -> success uid=${cleanUid.safeTail()}")
    }

    override suspend fun deleteProfile(uid: String) {
        val cleanUid = uid.trim()
        if (cleanUid.isEmpty()) return
        collection.document(cleanUid).delete().awaitResult()
        Log.d(TAG, "deleteProfile -> uid=${cleanUid.safeTail()}")
    }

    private fun DocumentSnapshot.toClientProfileOrNull(): ClientProfile? {
        return runCatching {
            ClientProfile(
                id = id.trim(),
                email = getString("email").orEmpty().trim(),
                appleUserIdentifier = getString("appleUserIdentifier").orEmpty().trim(),
                fullName = getString("fullName").orEmpty().trim(),
                phoneNumber = getString("phoneNumber").orEmpty().filter(Char::isDigit),
                birthday = getDateValue("birthday") ?: Date(0),
                address = getString("address").orEmpty().trim(),
                emergencyContactName = getString("emergencyContactName").orEmpty().trim(),
                emergencyContactPhone = getString("emergencyContactPhone").orEmpty()
                    .filter(Char::isDigit),
                isProfileComplete = getBoolean("isProfileComplete") == true || getBoolean("profileComplete") == true,
                createdAt = getDateValue("createdAt") ?: Date(),
                updatedAt = getDateValue("updatedAt") ?: Date(),
                profileCompletedAt = getDateValue("profileCompletedAt"),
                profileImageURL = getString("profileImageURL")?.trim()?.takeIf { it.isNotEmpty() },
                profileImagePath = getString("profileImagePath")?.trim()
                    ?.takeIf { it.isNotEmpty() },
            )
        }.onFailure { error ->
            Log.e(TAG, "toClientProfileOrNull -> mapping failed doc=${id.safeTail()}", error)
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

private fun String.safeTail(): String {
    val clean = trim()
    if (clean.isEmpty()) return "<empty>"
    return "…${clean.takeLast(6)}"
}
