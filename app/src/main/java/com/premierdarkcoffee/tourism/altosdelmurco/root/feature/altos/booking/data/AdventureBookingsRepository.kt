package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.data.AdventureBookingDto
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureAvailabilitySlot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBooking
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBookingRequest
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBookingStatus
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBuildPlan
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureCatalogRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventurePlanner
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureReservationItemDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationFoodDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.domain.AdventureBookingsRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardReferenceType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardsRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.util.constant.FirestoreCollections
import com.premierdarkcoffee.tourism.altosdelmurco.util.database.awaitResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class AdventureBookingsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val catalogRepository: AdventureCatalogRepositoriable,
    private val loyaltyRewardsRepository: LoyaltyRewardsRepositoriable,
) : AdventureBookingsRepositoriable {

    override fun observeBookings(
        nationalId: String,
    ): Flow<List<AdventureBooking>> = callbackFlow {
        val cleanNationalId = nationalId.filter(Char::isDigit)

        if (cleanNationalId.isEmpty()) {
            trySend(emptyList()).isSuccess
            close()
            return@callbackFlow
        }

        val registration = firestore
            .collection(FirestoreCollections.ADVENTURE_BOOKINGS)
            .whereEqualTo("nationalId", cleanNationalId)
            .orderBy("startAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val bookings = snapshot
                    ?.documents
                    .orEmpty()
                    .mapNotNull { document ->
                        document.toObject(AdventureBookingDto::class.java)
                            ?.toDomain(document.id)
                    }
                    .sortedBy { it.startAt.time }

                trySend(bookings).isSuccess
            }

        awaitClose { registration.remove() }
    }

    override suspend fun fetchAvailability(
        date: Date,
        items: List<AdventureReservationItemDraft>,
        foodReservation: ReservationFoodDraft?,
        packageDiscountAmount: Double,
    ): List<AdventureAvailabilitySlot> {
        val catalog = catalogRepository.fetchCatalog()

        return AdventurePlanner.buildAvailability(
            day = date,
            items = items,
            foodReservation = foodReservation,
            packageDiscountAmount = packageDiscountAmount,
            catalog = catalog,
        )
    }

    override suspend fun createBooking(request: AdventureBookingRequest): AdventureBooking {
        val catalog = catalogRepository.fetchCatalog()

        val basePlan = AdventurePlanner.buildPlan(
            day = request.date,
            startAt = request.selectedStartAt,
            items = request.items,
            foodReservation = request.foodReservation,
            packageDiscountAmount = request.packageDiscountAmount,
            catalog = catalog,
        ) ?: error("Invalid reservation configuration.")

        val rewardPreview = loyaltyRewardsRepository.previewAdventureRewards(
            nationalId = request.nationalId,
            activityItems = request.items,
            foodItems = request.foodReservation?.items.orEmpty(),
            catalog = catalog,
        )

        val finalPlan = AdventureBuildPlan(
            startAt = basePlan.startAt,
            endAt = basePlan.endAt,
            blocks = basePlan.blocks,
            adventureSubtotal = basePlan.adventureSubtotal,
            foodSubtotal = basePlan.foodSubtotal,
            subtotal = basePlan.subtotal,
            discountAmount = basePlan.discountAmount,
            loyaltyDiscountAmount = rewardPreview.totalDiscount,
            appliedRewards = rewardPreview.appliedRewards,
            nightPremium = basePlan.nightPremium,
            totalAmount = max(0.0, basePlan.totalAmount - rewardPreview.totalDiscount),
            hasNightPremium = basePlan.hasNightPremium,
        )

        val normalizedRequest = request.copy(
            nationalId = request.nationalId.filter(Char::isDigit),
            packageDiscountAmount = request.packageDiscountAmount.coerceAtLeast(0.0),
            loyaltyDiscountAmount = rewardPreview.totalDiscount.coerceAtLeast(0.0),
            appliedRewards = rewardPreview.appliedRewards,
        )

        val createdAt = Date()
        val bookingRef = firestore
            .collection(FirestoreCollections.ADVENTURE_BOOKINGS)
            .document()

        val dto = AdventureBookingDto.Companion.from(
            request = normalizedRequest,
            plan = finalPlan,
            createdAt = createdAt,
            status = AdventureBookingStatus.PENDING,
        )

        bookingRef.set(dto).awaitResult()

        loyaltyRewardsRepository.reserveRewards(
            nationalId = normalizedRequest.nationalId,
            referenceType = LoyaltyRewardReferenceType.BOOKING,
            referenceId = bookingRef.id,
            appliedRewards = normalizedRequest.appliedRewards,
        )

        return dto.toDomain(bookingRef.id)
    }

    override suspend fun cancelBooking(id: String, nationalId: String) {
        val cleanNationalId = nationalId.filter(Char::isDigit)
        val cleanId = id.trim()

        require(cleanId.isNotEmpty()) { "Booking id is required." }
        require(cleanNationalId.isNotEmpty()) { "No se encontró una cédula asociada a esta cuenta." }

        val bookingRef = firestore
            .collection(FirestoreCollections.ADVENTURE_BOOKINGS)
            .document(cleanId)

        val snapshot = bookingRef.get().awaitResult()

        if (!snapshot.exists()) {
            error("Booking not found.")
        }

        val dto = snapshot.toObject(AdventureBookingDto::class.java)
            ?: error("Could not read booking data.")

        if (dto.nationalId.filter(Char::isDigit) != cleanNationalId) {
            error("You are not allowed to cancel this booking.")
        }

        bookingRef
            .update("status", AdventureBookingStatus.CANCELED.rawValue)
            .awaitResult()

        loyaltyRewardsRepository.releaseRewards(
            nationalId = dto.nationalId,
            referenceId = cleanId,
        )
    }
}