package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadProfileStatsUseCase @Inject constructor(
    private val repository: ProfileStatsRepositoriable,
) {
    suspend fun execute(userId: String): ProfileStats = repository.loadStats(userId)
}

class ObserveProfileStatsUseCase @Inject constructor(
    private val repository: ProfileStatsRepositoriable,
) {
    fun execute(userId: String): Flow<ProfileStats> = repository.observeStats(userId)
}

class LoadProfileImageUseCase @Inject constructor(
    private val repository: ProfileImageRepositoriable,
) {
    suspend fun execute(profile: ClientProfile): ByteArray? {
        val cached = repository.cachedImageBytes(profile.id)
        if (cached != null) return cached

        val url = profile.profileImageURL?.trim().orEmpty()
        if (url.isEmpty()) return null

        return repository.downloadAndCacheImage(
            userId = profile.id,
            url = url,
        )
    }
}

class UploadProfileImageUseCase @Inject constructor(
    private val repository: ProfileImageRepositoriable,
) {
    suspend fun execute(profile: ClientProfile, bytes: ByteArray): UploadedProfileImage =
        repository.uploadProfileImage(profile = profile, bytes = bytes)
}

class DeleteProfileImageUseCase @Inject constructor(
    private val repository: ProfileImageRepositoriable,
) {
    suspend fun execute(profile: ClientProfile) {
        repository.deleteProfileImage(profile.profileImagePath)
        repository.removeCachedImage(profile.id)
    }
}
