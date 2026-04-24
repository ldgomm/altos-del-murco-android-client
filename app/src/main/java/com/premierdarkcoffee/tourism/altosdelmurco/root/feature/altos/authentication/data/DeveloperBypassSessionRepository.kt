package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.data

import com.premierdarkcoffee.tourism.altosdelmurco.BuildConfig
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionState
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfile
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Singleton
class DeveloperBypassSessionRepository @Inject constructor() : SessionRepositoriable {
    override fun sessionState(): Flow<SessionState> {
        return if (BuildConfig.DEBUG) {
            flowOf(
                SessionState.Authenticated(
                    profile = ClientProfile(
                        id = "developer-preview",
                        email = "developer@preview.local",
                        appleUserIdentifier = "",
                        fullName = "Developer Preview",
                        nationalId = "0000000000",
                        phoneNumber = "0000000000",
                        birthday = Date(),
                        address = "Preview",
                        emergencyContactName = "Preview",
                        emergencyContactPhone = "0000000000",
                        isProfileComplete = true,
                        createdAt = Date(),
                        updatedAt = Date(),
                        profileCompletedAt = Date(),
                        profileImageURL = null,
                        profileImagePath = null,
                    ),
                    developerBypass = true,
                ),
            )
        } else {
            flowOf(SessionState.Unauthenticated)
        }
    }

    override suspend fun refresh() = Unit
}
