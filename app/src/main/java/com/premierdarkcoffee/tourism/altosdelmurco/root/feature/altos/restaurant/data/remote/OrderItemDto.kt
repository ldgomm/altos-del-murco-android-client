package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote

import com.google.firebase.Timestamp
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderItemStatus
import java.util.Date
import java.util.UUID

data class OrderItemDto(
    val id: String = "",
    val groupId: String = "",
    val sourceCartItemId: String? = null,
    val menuItemId: String = "",
    val name: String = "",
    val itemDescription: String? = null,
    val unitPrice: Double = 0.0,
    val quantity: Int = 1,
    val notes: String? = null,
    val status: String = OrderItemStatus.PENDING.rawValue,
    val createdAt: Timestamp = Timestamp.now(),
    val preparingAt: Timestamp? = null,
    val readyForDeliveryAt: Timestamp? = null,
    val deliveredAt: Timestamp? = null,
    val canceledAt: Timestamp? = null,
    val canceledReason: String? = null,
) {
    constructor(domain: OrderItem) : this(
        id = domain.id,
        groupId = domain.groupId,
        sourceCartItemId = domain.sourceCartItemId,
        menuItemId = domain.menuItemId,
        name = domain.name,
        itemDescription = domain.itemDescription,
        unitPrice = domain.unitPrice,
        quantity = 1,
        notes = domain.notes,
        status = domain.status.rawValue,
        createdAt = Timestamp(domain.createdAt),
        preparingAt = domain.preparingAt?.let(::Timestamp),
        readyForDeliveryAt = domain.readyForDeliveryAt?.let(::Timestamp),
        deliveredAt = domain.deliveredAt?.let(::Timestamp),
        canceledAt = domain.canceledAt?.let(::Timestamp),
        canceledReason = domain.canceledReason,
    )

    fun toDomain(): OrderItem = OrderItem(
        id = id.ifBlank { UUID.randomUUID().toString() },
        groupId = groupId.ifBlank { UUID.randomUUID().toString() },
        sourceCartItemId = sourceCartItemId?.trim()?.takeIf { it.isNotEmpty() },
        menuItemId = menuItemId.trim(),
        name = name.trim().ifBlank { "Plato" },
        itemDescription = itemDescription?.trim()?.takeIf { it.isNotEmpty() },
        unitPrice = unitPrice.coerceAtLeast(0.0),
        quantity = 1,
        notes = notes?.trim()?.takeIf { it.isNotEmpty() },
        status = OrderItemStatus.fromRaw(status),
        createdAt = createdAt.toDateOrNow(),
        preparingAt = preparingAt?.toDate(),
        readyForDeliveryAt = readyForDeliveryAt?.toDate(),
        deliveredAt = deliveredAt?.toDate(),
        canceledAt = canceledAt?.toDate(),
        canceledReason = canceledReason?.trim()?.takeIf { it.isNotEmpty() },
    )
}

private fun Timestamp?.toDateOrNow(): Date = this?.toDate() ?: Date()