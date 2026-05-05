package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.presentation.viewmodel

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBooking
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBookingStatus
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureDateHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class AdventureReservationTimelineFilter(
    val title: String,
) {
    ALL("Todas"),
    CURRENT("Actuales"),
    FUTURE("Futuras"),
    PAST("Pasadas"),
}

enum class AdventureReservationStatusFilter(
    val title: String,
    val bookingStatus: AdventureBookingStatus?,
) {
    ALL("Todo", null),
    PENDING("Pendiente", AdventureBookingStatus.PENDING),
    CONFIRMED("Confirmada", AdventureBookingStatus.CONFIRMED),
    COMPLETED("Completada", AdventureBookingStatus.COMPLETED),
    CANCELED("Cancelada", AdventureBookingStatus.CANCELED),
}

enum class AdventureReservationSortOrder(
    val title: String,
) {
    NEAREST_FIRST("Próximas primero"),
    NEWEST_FIRST("Más recientes"),
    OLDEST_FIRST("Más antiguas"),
}

data class AdventureBookingsDateGroup(
    val id: String,
    val date: Date,
    val bookings: List<AdventureBooking>,
) {
    val title: String
        get() {
            val calendar = Calendar.getInstance()
            val today = AdventureDateHelper.startOfDay(Date())
            val tomorrow = calendar.apply {
                time = today
                add(Calendar.DAY_OF_YEAR, 1)
            }.time
            val yesterday = calendar.apply {
                time = today
                add(Calendar.DAY_OF_YEAR, -1)
            }.time

            return when {
                AdventureDateHelper.sameDay(date, today) -> "Hoy"
                AdventureDateHelper.sameDay(date, tomorrow) -> "Mañana"
                AdventureDateHelper.sameDay(date, yesterday) -> "Ayer"
                else -> longDateFormatter.format(date).replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale("es", "EC")) else it.toString()
                }
            }
        }

    companion object {
        private val longDateFormatter = SimpleDateFormat(
            "EEEE d 'de' MMMM yyyy",
            Locale("es", "EC"),
        )
    }
}

data class AdventureBookingsUiState(
    val userId: String = "",
    val allBookings: List<AdventureBooking> = emptyList(),
    val selectedTimelineFilter: AdventureReservationTimelineFilter = AdventureReservationTimelineFilter.ALL,
    val selectedStatusFilter: AdventureReservationStatusFilter = AdventureReservationStatusFilter.ALL,
    val sortOrder: AdventureReservationSortOrder = AdventureReservationSortOrder.NEAREST_FIRST,
    val now: Date = Date(),
    val isLoading: Boolean = false,
    val isCancelling: Boolean = false,
    val cancellingBookingId: String? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
) {
    val displayedBookings: List<AdventureBooking>
        get() {
            val filtered = allBookings.filter { booking ->
                matchesTimelineFilter(booking) && matchesStatusFilter(booking)
            }

            return sorted(filtered)
        }

    val groupedBookings: List<AdventureBookingsDateGroup>
        get() {
            val groups = linkedMapOf<String, MutableList<AdventureBooking>>()

            displayedBookings.forEach { booking ->
                val day = AdventureDateHelper.startOfDay(booking.startAt)
                val key = AdventureDateHelper.dayKey(day)
                groups.getOrPut(key) { mutableListOf() }.add(booking)
            }

            return groups.map { (key, bookings) ->
                AdventureBookingsDateGroup(
                    id = key,
                    date = AdventureDateHelper.startOfDay(bookings.first().startAt),
                    bookings = bookings,
                )
            }
        }

    val totalCount: Int
        get() = allBookings.size

    val displayedCount: Int
        get() = displayedBookings.size

    val currentCount: Int
        get() = allBookings.count { isCurrent(it) }

    val futureCount: Int
        get() = allBookings.count { isFuture(it) }

    val pastCount: Int
        get() = allBookings.count { isPast(it) }

    private fun matchesTimelineFilter(booking: AdventureBooking): Boolean {
        return when (selectedTimelineFilter) {
            AdventureReservationTimelineFilter.ALL -> true
            AdventureReservationTimelineFilter.CURRENT -> isCurrent(booking)
            AdventureReservationTimelineFilter.FUTURE -> isFuture(booking)
            AdventureReservationTimelineFilter.PAST -> isPast(booking)
        }
    }

    private fun matchesStatusFilter(booking: AdventureBooking): Boolean {
        val selectedStatus = selectedStatusFilter.bookingStatus ?: return true
        return booking.status == selectedStatus
    }

    private fun sorted(bookings: List<AdventureBooking>): List<AdventureBooking> {
        return when (sortOrder) {
            AdventureReservationSortOrder.NEAREST_FIRST -> bookings.sortedWith { lhs, rhs ->
                val lhsRank = timelineRank(lhs)
                val rhsRank = timelineRank(rhs)

                when {
                    lhsRank != rhsRank -> lhsRank.compareTo(rhsRank)
                    lhsRank == 2 -> rhs.startAt.time.compareTo(lhs.startAt.time)
                    lhs.startAt.time != rhs.startAt.time -> lhs.startAt.time.compareTo(rhs.startAt.time)
                    else -> lhs.createdAt.time.compareTo(rhs.createdAt.time)
                }
            }

            AdventureReservationSortOrder.NEWEST_FIRST -> bookings.sortedWith(
                compareByDescending<AdventureBooking> { it.startAt.time }
                    .thenByDescending { it.createdAt.time },
            )

            AdventureReservationSortOrder.OLDEST_FIRST -> bookings.sortedWith(
                compareBy<AdventureBooking> { it.startAt.time }
                    .thenBy { it.createdAt.time },
            )
        }
    }

    private fun timelineRank(booking: AdventureBooking): Int {
        return when {
            isCurrent(booking) -> 0
            isFuture(booking) -> 1
            else -> 2
        }
    }

    private fun isCurrent(booking: AdventureBooking): Boolean {
        val isActiveStatus = booking.status == AdventureBookingStatus.PENDING ||
                booking.status == AdventureBookingStatus.CONFIRMED

        return isActiveStatus &&
                !booking.startAt.after(now) &&
                !booking.endAt.before(now)
    }

    private fun isFuture(booking: AdventureBooking): Boolean {
        val isActiveStatus = booking.status == AdventureBookingStatus.PENDING ||
                booking.status == AdventureBookingStatus.CONFIRMED

        return isActiveStatus && booking.startAt.after(now)
    }

    private fun isPast(booking: AdventureBooking): Boolean {
        return booking.endAt.before(now) ||
                booking.status == AdventureBookingStatus.COMPLETED ||
                booking.status == AdventureBookingStatus.CANCELED
    }
}
