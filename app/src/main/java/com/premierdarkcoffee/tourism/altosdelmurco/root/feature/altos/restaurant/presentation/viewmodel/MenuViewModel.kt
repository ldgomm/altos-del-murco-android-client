package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardEngine
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardsRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardMenuLine
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentation
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentationFactory
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardWalletSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuCategory
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuSection
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.ObserveMenuUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val observeMenuUseCase: ObserveMenuUseCase,
    private val loyaltyRewardsRepository: LoyaltyRewardsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null
    private var lastRewardsNationalId: String? = null

    fun onAppear(nationalId: String?) {
        if (observeJob == null) {
            observeJob = viewModelScope.launch {
                observeMenuUseCase.execute().collectLatest { sections ->
                    val sorted = sortSections(sections)
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            sections = sorted,
                            selectedCategoryId = current.selectedCategoryId
                                ?.takeIf { selected -> sorted.any { it.category.id == selected } }
                                ?: sorted.firstOrNull()?.category?.id,
                            errorMessage = null,
                        )
                    }
                }
            }
        }

        refreshRewards(nationalId)
    }

    fun onCategorySelected(categoryId: String?) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun currentLevelTitle(): String = _uiState.value.walletSnapshot.currentLevel.title

    fun rewardPresentation(
        item: MenuItem,
        quantity: Int = 1,
    ): RewardPresentation? {
        val projected = projectedRewardResult(item, quantity)

        projected.appliedRewards
            .firstOrNull { it.affectedMenuItemIds.contains(item.id) }
            ?.let { return RewardPresentation.fromAppliedReward(it) }

        return RewardPresentationFactory.menuPresentation(
            item = item,
            wallet = _uiState.value.walletSnapshot,
        )
    }

    fun incrementalDiscount(
        item: MenuItem,
        quantity: Int = 1,
    ): Double = projectedRewardResult(item, quantity).totalDiscount.roundMoney()

    fun displayedPrice(
        item: MenuItem,
        quantity: Int = 1,
    ): Double {
        val subtotal = (item.finalPrice * quantity.coerceAtLeast(1)).roundMoney()
        return (subtotal - incrementalDiscount(item, quantity)).coerceAtLeast(0.0).roundMoney()
    }

    private fun projectedRewardResult(
        item: MenuItem,
        quantity: Int,
    ) = LoyaltyRewardEngine.evaluateRestaurant(
        templates = _uiState.value.walletSnapshot.availableTemplates,
        wallet = _uiState.value.walletSnapshot,
        menuLines = listOf(
            RewardMenuLine(
                menuItemId = item.id,
                name = item.name,
                unitPrice = item.finalPrice,
                quantity = quantity.coerceAtLeast(1),
            ),
        ),
    )

    private fun refreshRewards(nationalId: String?) {
        val cleanNationalId = nationalId?.filter { it.isDigit() }.orEmpty()
        if (cleanNationalId.isEmpty()) {
            _uiState.update {
                it.copy(walletSnapshot = RewardWalletSnapshot.empty(""))
            }
            lastRewardsNationalId = null
            return
        }

        if (lastRewardsNationalId == cleanNationalId &&
            _uiState.value.walletSnapshot.nationalId == cleanNationalId
        ) {
            return
        }

        lastRewardsNationalId = cleanNationalId

        viewModelScope.launch {
            runCatching {
                loyaltyRewardsRepository.loadWalletSnapshot(cleanNationalId)
            }.onSuccess { snapshot ->
                _uiState.update { it.copy(walletSnapshot = snapshot, errorMessage = null) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        walletSnapshot = RewardWalletSnapshot.empty(cleanNationalId),
                        errorMessage = error.message ?: "No se pudieron cargar tus beneficios.",
                    )
                }
            }
        }
    }

    private fun sortSections(sections: List<MenuSection>): List<MenuSection> {
        val preferredOrder = listOf(
            "Entradas",
            "Sopas",
            "Platos Fuertes",
            "Extras",
            "Postres",
            "Bebidas",
            "Bebidas Alcohólicas",
        )

        return sections.sortedWith(
            compareBy<MenuSection> {
                val index = preferredOrder.indexOf(it.category.title)
                if (index == -1) Int.MAX_VALUE else index
            }.thenBy { it.category.title },
        )
    }

    private fun Double.roundMoney(): Double = kotlin.math.round(this * 100.0) / 100.0
}