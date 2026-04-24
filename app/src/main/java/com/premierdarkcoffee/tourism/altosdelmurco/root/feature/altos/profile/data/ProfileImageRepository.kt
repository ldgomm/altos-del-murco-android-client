package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.data

import android.content.Context
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfile
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ProfileImageRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.UploadedProfileImage
import com.premierdarkcoffee.tourism.altosdelmurco.util.database.awaitResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileImageRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val storage: FirebaseStorage,
) : ProfileImageRepositoriable {

    private val cacheDirectory: File by lazy {
        File(context.cacheDir, "ProfileImages").apply {
            if (!exists()) mkdirs()
        }
    }

    override suspend fun cachedImageBytes(userId: String): ByteArray? =
        withContext(Dispatchers.IO) {
            val file = fileFor(userId)
            if (file.exists()) runCatching { file.readBytes() }.getOrNull() else null
        }

    override suspend fun downloadAndCacheImage(userId: String, url: String): ByteArray? =
        withContext(Dispatchers.IO) {
            val cleanUrl = url.trim()
            if (userId.isBlank() || cleanUrl.isBlank()) return@withContext null

            runCatching {
                val connection = URL(cleanUrl).openConnection() as HttpURLConnection
                connection.connectTimeout = 12_000
                connection.readTimeout = 12_000
                connection.instanceFollowRedirects = true
                connection.inputStream.use { input ->
                    val bytes = input.readBytes()
                    saveImageBytes(userId, bytes)
                }
            }.getOrNull()
        }

    override suspend fun saveImageBytes(userId: String, bytes: ByteArray): ByteArray =
        withContext(Dispatchers.IO) {
            if (userId.isNotBlank() && bytes.isNotEmpty()) {
                fileFor(userId).writeBytes(bytes)
            }
            bytes
        }

    override suspend fun removeCachedImage(userId: String) = withContext(Dispatchers.IO) {
        if (userId.isNotBlank()) fileFor(userId).delete()
    }

    override suspend fun uploadProfileImage(
        profile: ClientProfile,
        bytes: ByteArray,
    ): UploadedProfileImage {
        require(profile.id.isNotBlank()) { "User id is required to upload a profile image." }
        require(bytes.isNotEmpty()) { "Profile image data is empty." }

        val cleanUserId = profile.id.trim()
        val path = "profile_images/$cleanUserId/avatar_${System.currentTimeMillis()}.jpg"
        val ref = storage.reference.child(path)

        ref.putBytes(bytes).awaitResult()
        val downloadUrl = ref.downloadUrl.awaitResult().toString()

        if (!profile.profileImagePath.isNullOrBlank() && profile.profileImagePath != path) {
            runCatching { deleteProfileImage(profile.profileImagePath) }
        }

        saveImageBytes(cleanUserId, bytes)

        return UploadedProfileImage(
            downloadURL = downloadUrl,
            storagePath = path,
        )
    }

    override suspend fun deleteProfileImage(storagePath: String?) {
        val cleanPath = storagePath?.trim().orEmpty()
        if (cleanPath.isEmpty()) return

        val ref = storage.reference.child(cleanPath)
        runCatching {
            ref.delete().awaitResult()
        }.onFailure { error ->
            val storageError = error as? StorageException
            if (storageError?.errorCode != StorageException.ERROR_OBJECT_NOT_FOUND) {
                throw error
            }
        }
    }

    private fun fileFor(userId: String): File =
        File(cacheDirectory, "profile_${userId.trim()}.jpg")
}