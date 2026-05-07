package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfile
import java.util.Date

//class ResolveSessionUseCase(
//    private val authRepository: AuthenticationRepositoriable,
//    private val clientProfileRepository: ClientProfileRepositoriable,
//) {
//    suspend fun execute(): SessionDestination {
//        val user = authRepository.currentUser() ?: return SessionDestination.SignedOut
//        return execute(user)
//    }
//
//    suspend fun execute(user: AuthenticatedUser): SessionDestination {
//        val profile = clientProfileRepository.fetchProfile(user.uid)
//
//        return when {
//            profile == null -> SessionDestination.NeedsProfile(user = user, profile = null)
//            profile.isComplete -> SessionDestination.Authenticated(profile)
//            else -> SessionDestination.NeedsProfile(user = user, profile = profile)
//        }
//    }
//}

class ResolveSessionUseCase(
    private val authRepository: AuthenticationRepositoriable,
    private val clientProfileRepository: ClientProfileRepositoriable,
) {
    suspend fun execute(): SessionDestination {
        val user = authRepository.currentUser() ?: return SessionDestination.SignedOut
        return execute(user)
    }

    suspend fun execute(user: AuthenticatedUser): SessionDestination {
        val existingProfile = clientProfileRepository.fetchProfile(user.uid)

        // Temporary business rule:
        // Profile completion is NOT mandatory. Any valid authenticated Firebase user
        // can enter the app immediately. If Firestore does not have a client profile yet,
        // we create an in-memory fallback profile only for the current session.
        // This does not write incomplete profile data to Firestore.
        return SessionDestination.Authenticated(
            profile = existingProfile ?: user.toSessionFallbackProfile(),
        )
    }

    private fun AuthenticatedUser.toSessionFallbackProfile(): ClientProfile {
        val now = Date()
        val display = displayName.trim()
            .ifBlank { email.substringBefore("@").trim() }
            .ifBlank { "Cliente" }

        return ClientProfile(
            id = uid.trim(),
            email = email.trim(),
            appleUserIdentifier = appleUserIdentifier.trim(),
            fullName = display,
            phoneNumber = "",
            birthday = Date(0),
            address = "",
            emergencyContactName = "",
            emergencyContactPhone = "",
            isProfileComplete = false,
            createdAt = now,
            updatedAt = now,
            profileCompletedAt = null,
            profileImageURL = null,
            profileImagePath = null,
        )
    }
}
