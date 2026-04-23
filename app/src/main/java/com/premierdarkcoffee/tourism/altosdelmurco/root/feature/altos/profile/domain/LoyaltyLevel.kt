package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain

enum class LoyaltyLevel(
    val title: String,
    val systemImage: String,
    val badgeSubtitle: String,
    val minimumSpent: Double,
    val spendRangeText: String,
    val benefits: List<String>,
) {
    BRONZE(
        title = "Bronce",
        systemImage = "sparkles",
        badgeSubtitle = "Tus primeras visitas ya empiezan a premiarte",
        minimumSpent = 0.0,
        spendRangeText = "De $0 a $99",
        benefits = listOf(
            "Acceso al programa Murco Loyalty",
            "Primeras promociones automáticas",
        ),
    ),
    SILVER(
        title = "Plata",
        systemImage = "seal.fill",
        badgeSubtitle = "Más beneficios cada vez que vuelves",
        minimumSpent = 100.0,
        spendRangeText = "De $100 a $299",
        benefits = listOf(
            "Más promociones activas por nivel",
            "Descuentos más frecuentes en restaurante y aventura",
        ),
    ),
    GOLD(
        title = "Oro",
        systemImage = "star.circle.fill",
        badgeSubtitle = "Descuentos más fuertes y regalos más frecuentes",
        minimumSpent = 300.0,
        spendRangeText = "De $300 a $799",
        benefits = listOf(
            "Premios de mayor valor",
            "Más opciones de items gratis o porcentaje off",
        ),
    ),
    PLATINUM(
        title = "Platino",
        systemImage = "crown.fill",
        badgeSubtitle = "Nivel preferente con premios premium",
        minimumSpent = 800.0,
        spendRangeText = "De $800 a $1499",
        benefits = listOf(
            "Beneficios premium",
            "Prioridad para recompensas más fuertes",
        ),
    ),
    DIAMOND(
        title = "Diamante",
        systemImage = "diamond.fill",
        badgeSubtitle = "Nuestro máximo nivel para clientes top",
        minimumSpent = 1500.0,
        spendRangeText = "Desde $1500",
        benefits = listOf(
            "Máximo nivel de beneficios",
            "Acceso continuo a recompensas top",
        ),
    );

    val nextLevel: LoyaltyLevel?
        get() = entries.getOrNull(ordinal + 1)

    companion object {
        fun fromTotalSpent(totalSpent: Double): LoyaltyLevel = entries.lastOrNull {
            totalSpent >= it.minimumSpent
        } ?: BRONZE

        fun progress(totalSpent: Double): Double {
            val current = fromTotalSpent(totalSpent)
            val next = current.nextLevel ?: return 1.0
            val span = (next.minimumSpent - current.minimumSpent).coerceAtLeast(1.0)
            return ((totalSpent - current.minimumSpent) / span).coerceIn(0.0, 1.0)
        }
    }
}
