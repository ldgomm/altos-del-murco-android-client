package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

data class MenuSection(
    val id: String,
    val category: MenuCategory,
    val items: List<MenuItem>,
)
