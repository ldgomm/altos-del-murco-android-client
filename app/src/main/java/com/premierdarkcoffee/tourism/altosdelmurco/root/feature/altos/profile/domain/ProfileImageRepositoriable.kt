package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain

data class UploadedProfileImage(
    val downloadURL: String,
    val storagePath: String,
)

interface ProfileImageRepositoriable {
    suspend fun cachedImageBytes(userId: String): ByteArray?
    suspend fun downloadAndCacheImage(userId: String, url: String): ByteArray?
    suspend fun saveImageBytes(userId: String, bytes: ByteArray): ByteArray
    suspend fun removeCachedImage(userId: String)
    suspend fun uploadProfileImage(profile: ClientProfile, bytes: ByteArray): UploadedProfileImage
    suspend fun deleteProfileImage(storagePath: String?)
}
