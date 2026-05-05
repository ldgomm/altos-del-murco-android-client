package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.data.AdventureBookingDto
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBookingStatus
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyLevel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardsRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ProfileStats
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ProfileStatsRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote.OrderDto
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderStatus
import com.premierdarkcoffee.tourism.altosdelmurco.util.constant.FirestoreCollections
import com.premierdarkcoffee.tourism.altosdelmurco.util.database.awaitResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.round

@Singleton
class ProfileStatsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val loyaltyRewardsRepository: LoyaltyRewardsRepositoriable,
) : ProfileStatsRepositoriable {

    override suspend fun loadStats(userId: String): ProfileStats {
        val cleanUserId = userId.trim()
        if (cleanUserId.isEmpty()) return ProfileStats.EMPTY

        val orderSnapshot = firestore.collection(FirestoreCollections.RESTAURANT_ORDERS)
            .whereEqualTo("userId", cleanUserId).get().awaitResult()

        val bookingSnapshot = firestore.collection(FirestoreCollections.ADVENTURE_BOOKINGS)
            .whereEqualTo("userId", cleanUserId).get().awaitResult()

        val wallet = loyaltyRewardsRepository.loadWalletSnapshot(cleanUserId)

        val completedOrders = orderSnapshot.documents.mapNotNull { document ->
            document.toObject(OrderDto::class.java)?.toDomain()
        }.filter { order -> order.status == OrderStatus.COMPLETED }

        val completedBookings = bookingSnapshot.documents.mapNotNull { document ->
            document.toObject(AdventureBookingDto::class.java)?.toDomain(document.id)
        }.filter { booking -> booking.status == AdventureBookingStatus.COMPLETED }

        val restaurantSpent = completedOrders.sumOf { it.totalAmount }.roundMoney()
        val adventureSpent = completedBookings.sumOf { it.totalAmount }.roundMoney()
        val totalSpent = (restaurantSpent + adventureSpent).roundMoney()
        val computedLevel = LoyaltyLevel.fromTotalSpent(totalSpent)

        return ProfileStats(
            points = wallet.points.coerceAtLeast(totalSpent.toInt()),
            completedOrders = completedOrders.size,
            completedBookings = completedBookings.size,
            restaurantSpent = restaurantSpent,
            adventureSpent = adventureSpent,
            totalSpent = totalSpent,
            level = computedLevel,
            wallet = wallet.copy(
                currentLevel = computedLevel,
                totalSpent = totalSpent,
                points = wallet.points.coerceAtLeast(totalSpent.toInt()),
            ),
        )
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    override fun observeStats(userId: String): Flow<ProfileStats> = callbackFlow {
        val cleanUserId = userId.trim()
        if (cleanUserId.isEmpty()) {
            trySend(ProfileStats.EMPTY).isSuccess
            close()
            return@callbackFlow
        }

        val refreshRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 32)
        val registrations = mutableListOf<ListenerRegistration>()

        fun requestRefresh() {
            refreshRequests.tryEmit(Unit)
        }

        val loaderJob: Job = launch {
            refreshRequests.onStart { emit(Unit) }.debounce(160)
                .mapLatest { loadStats(cleanUserId) }.catch { error ->
                    if (error is CancellationException) throw error
                    close(error)
                }.collect { stats ->
                    trySend(stats).isSuccess
                }
        }

        registrations += firestore.collection(FirestoreCollections.RESTAURANT_ORDERS)
            .whereEqualTo("userId", cleanUserId).addSnapshotListener { _, error ->
                if (error != null) close(error) else requestRefresh()
            }

        registrations += firestore.collection(FirestoreCollections.ADVENTURE_BOOKINGS)
            .whereEqualTo("userId", cleanUserId).addSnapshotListener { _, error ->
                if (error != null) close(error) else requestRefresh()
            }

        val walletJob =
            loyaltyRewardsRepository.observeWalletSnapshot(cleanUserId).onEach { requestRefresh() }
                .catch { error ->
                    if (error is CancellationException) throw error
                    close(error)
                }.launchIn(this)

        awaitClose {
            registrations.forEach { it.remove() }
            walletJob.cancel()
            loaderJob.cancel()
        }
    }
}


private fun Double.roundMoney(): Double = round(this * 100.0) / 100.0
