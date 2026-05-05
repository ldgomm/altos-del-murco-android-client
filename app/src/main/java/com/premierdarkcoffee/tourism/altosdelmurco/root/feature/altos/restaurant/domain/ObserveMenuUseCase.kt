package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

import javax.inject.Inject

class ObserveMenuUseCase @Inject constructor(private val repository: MenuRepositoriable) {
    fun execute() = repository.observeMenu()
}