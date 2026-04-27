package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.domain

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBooking
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBookingStatus
import java.util.Date
import java.util.concurrent.TimeUnit

object AdventureBookingCancellationPolicy {
    private val minimumNoticeMillis: Long = TimeUnit.HOURS.toMillis(2)

    fun canClientCancel(
        booking: AdventureBooking,
        now: Date = Date(),
    ): Boolean {
        return reasonClientCannotCancel(booking, now) == null
    }

    fun reasonClientCannotCancel(
        booking: AdventureBooking,
        now: Date = Date(),
    ): String? {
        if (booking.status !in setOf(
                AdventureBookingStatus.PENDING, AdventureBookingStatus.CONFIRMED
            )
        ) {
            return "Solo se pueden cancelar reservas pendientes o confirmadas."
        }

        if (!booking.startAt.after(now)) {
            return "Esta reserva ya inició o ya pasó."
        }

        val latestAllowed = Date(booking.startAt.time - minimumNoticeMillis)
        if (now.after(latestAllowed)) {
            return "Para cancelar con menos de 2 horas de anticipación, por favor contáctanos por WhatsApp."
        }

        return null
    }
}
