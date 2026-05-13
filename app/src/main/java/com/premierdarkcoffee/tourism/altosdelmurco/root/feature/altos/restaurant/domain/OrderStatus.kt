package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

private fun String.normalizedOrderStatusKey(): String =
    filter(Char::isLetterOrDigit).lowercase()

enum class OrderStatus(
    val rawValue: String,
    val title: String,
    val clientTitle: String,
) {
    PENDING(
        rawValue = "pending",
        title = "Pendiente",
        clientTitle = "Pedido enviado",
    ),
    CONFIRMED(
        rawValue = "confirmed",
        title = "Confirmado",
        clientTitle = "Pedido confirmado",
    ),
    PREPARING(
        rawValue = "preparing",
        title = "En cocina",
        clientTitle = "En cocina",
    ),
    READY_FOR_PAYMENT(
        rawValue = "readyForPayment",
        title = "Listo para cobrar",
        clientTitle = "Pedido servido / listo para pagar",
    ),
    PAID(
        rawValue = "paid",
        title = "Pagado",
        clientTitle = "Pagado",
    ),
    CANCELED(
        rawValue = "canceled",
        title = "Cancelado",
        clientTitle = "Cancelado",
    );

    val isTerminal: Boolean
        get() = this == PAID || this == CANCELED

    val canClientCancel: Boolean
        get() = this == PENDING

    companion object {
        fun fromRaw(rawValue: String?): OrderStatus {
            val key = rawValue?.normalizedOrderStatusKey().orEmpty()

            return entries.firstOrNull {
                it.rawValue.normalizedOrderStatusKey() == key ||
                        it.name.normalizedOrderStatusKey() == key
            } ?: PENDING
        }
    }
}