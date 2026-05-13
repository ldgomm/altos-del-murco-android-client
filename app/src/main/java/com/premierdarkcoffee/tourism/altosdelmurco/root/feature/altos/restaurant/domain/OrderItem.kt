package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

import java.util.Date
import java.util.UUID
import kotlin.math.round

data class OrderItem(
    val id: String = UUID.randomUUID().toString(),
    val groupId: String = UUID.randomUUID().toString(),
    val sourceCartItemId: String? = null,
    val menuItemId: String,
    val name: String,
    val itemDescription: String? = null,
    val unitPrice: Double,
    val quantity: Int = 1,
    val notes: String? = null,
    val status: OrderItemStatus = OrderItemStatus.PENDING,
    val createdAt: Date = Date(),
    val preparingAt: Date? = null,
    val readyForDeliveryAt: Date? = null,
    val deliveredAt: Date? = null,
    val canceledAt: Date? = null,
    val canceledReason: String? = null,
) {
    init {
        require(menuItemId.trim().isNotEmpty()) { "menuItemId cannot be blank" }
        require(name.trim().isNotEmpty()) { "name cannot be blank" }
    }

    val normalizedQuantity: Int
        get() = 1

    val safeGroupId: String
        get() = groupId.trim().ifBlank { UUID.randomUUID().toString() }

    val totalPrice: Double
        get() = (unitPrice.coerceAtLeast(0.0) * 1.0).roundMoney()

    val isActive: Boolean
        get() = status.isActive

    val isStarted: Boolean
        get() = status.hasStarted

    val isDelivered: Boolean
        get() = status == OrderItemStatus.DELIVERED

    val displayQuantityText: String
        get() = "1x"

    val lifecycleDateForSorting: Date
        get() = readyForDeliveryAt ?: deliveredAt ?: preparingAt ?: createdAt

    fun updatingStatus(
        newStatus: OrderItemStatus,
        now: Date = Date(),
        reason: String? = null,
    ): OrderItem {
        val cleanReason = reason?.trim()?.takeIf { it.isNotEmpty() }

        return when (newStatus) {
            OrderItemStatus.PENDING -> copy(
                quantity = 1,
                status = OrderItemStatus.PENDING,
                preparingAt = null,
                readyForDeliveryAt = null,
                deliveredAt = null,
                canceledAt = null,
                canceledReason = null,
            )

            OrderItemStatus.PREPARING -> copy(
                quantity = 1,
                status = OrderItemStatus.PREPARING,
                preparingAt = preparingAt ?: now,
                readyForDeliveryAt = null,
                deliveredAt = null,
                canceledAt = null,
                canceledReason = null,
            )

            OrderItemStatus.READY_FOR_DELIVERY -> copy(
                quantity = 1,
                status = OrderItemStatus.READY_FOR_DELIVERY,
                preparingAt = preparingAt ?: now,
                readyForDeliveryAt = readyForDeliveryAt ?: now,
                deliveredAt = null,
                canceledAt = null,
                canceledReason = null,
            )

            OrderItemStatus.DELIVERED -> copy(
                quantity = 1,
                status = OrderItemStatus.DELIVERED,
                preparingAt = preparingAt ?: now,
                readyForDeliveryAt = readyForDeliveryAt ?: now,
                deliveredAt = deliveredAt ?: now,
                canceledAt = null,
                canceledReason = null,
            )

            OrderItemStatus.CANCELED -> copy(
                quantity = 1,
                status = OrderItemStatus.CANCELED,
                canceledAt = canceledAt ?: now,
                canceledReason = cleanReason,
            )
        }
    }

    fun replacingCommercialFields(
        name: String,
        itemDescription: String?,
        unitPrice: Double,
        notes: String?,
    ): OrderItem = copy(
        name = name.trim(),
        itemDescription = itemDescription?.trim()?.takeIf { it.isNotEmpty() },
        unitPrice = unitPrice.coerceAtLeast(0.0).roundMoney(),
        quantity = 1,
        notes = notes?.trim()?.takeIf { it.isNotEmpty() },
    )

    companion object {
        fun normalizedUnits(
            sourceCartItemId: String? = null,
            menuItemId: String,
            name: String,
            itemDescription: String? = null,
            unitPrice: Double,
            quantity: Int,
            notes: String? = null,
            createdAt: Date = Date(),
        ): List<OrderItem> {
            val safeQuantity = quantity.coerceAtLeast(1)
            val groupId = UUID.randomUUID().toString()

            return List(safeQuantity) {
                OrderItem(
                    groupId = groupId,
                    sourceCartItemId = sourceCartItemId?.trim()?.takeIf { it.isNotEmpty() },
                    menuItemId = menuItemId.trim(),
                    name = name.trim(),
                    itemDescription = itemDescription?.trim()?.takeIf { it.isNotEmpty() },
                    unitPrice = unitPrice.coerceAtLeast(0.0).roundMoney(),
                    quantity = 1,
                    notes = notes?.trim()?.takeIf { it.isNotEmpty() },
                    status = OrderItemStatus.PENDING,
                    createdAt = createdAt,
                )
            }
        }
    }
}

private fun Double.roundMoney(): Double = round(this * 100.0) / 100.0