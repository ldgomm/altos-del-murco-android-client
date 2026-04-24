package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.AppliedReward
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardEngine
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardRuleType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardTemplate
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardsRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardComputationResult
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardMenuLine
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentation
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentationFactory
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardWalletSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuSection
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.ObserveMenuUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val observeMenuUseCase: ObserveMenuUseCase,
    private val loyaltyRewardsRepository: LoyaltyRewardsRepositoriable,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    private var menuJob: Job? = null
    private var rewardsJob: Job? = null
    private var currentRewardsNationalId: String? = null

    fun onAppear(nationalId: String?) {
        startMenuObservationIfNeeded()
        setNationalId(nationalId)
    }

    fun setNationalId(nationalId: String?) {
        val cleanNationalId = nationalId?.filter { it.isDigit() }.orEmpty()

        if (cleanNationalId.isEmpty()) {
            rewardsJob?.cancel()
            rewardsJob = null
            currentRewardsNationalId = null
            _uiState.update {
                it.copy(
                    isLoadingRewards = false,
                    walletSnapshot = RewardWalletSnapshot.empty(""),
                )
            }
            return
        }

        if (currentRewardsNationalId == cleanNationalId && rewardsJob?.isActive == true) return

        currentRewardsNationalId = cleanNationalId
        rewardsJob?.cancel()
        rewardsJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingRewards = true, errorMessage = null) }
            loyaltyRewardsRepository
                .observeWalletSnapshot(cleanNationalId)
                .collectLatest { wallet ->
                    _uiState.update {
                        it.copy(
                            walletSnapshot = wallet,
                            isLoadingRewards = false,
                            errorMessage = null,
                        )
                    }
                }
        }
    }

    fun onCategorySelected(categoryId: String?) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun currentLevelTitle(): String = _uiState.value.walletSnapshot.currentLevel.title

    val restaurantRewardTemplates: List<LoyaltyRewardTemplate>
        get() = _uiState.value.restaurantRewardTemplates

    fun rewardPresentation(item: MenuItem): RewardPresentation? =
        rewardPresentation(item, quantity = 1)

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

    fun displayedPrice(
        item: MenuItem,
        quantity: Int = 1,
    ): Double {
        val subtotal = (item.finalPrice * quantity.coerceAtLeast(1)).roundMoney()
        return (subtotal - incrementalDiscount(item, quantity)).coerceAtLeast(0.0).roundMoney()
    }

    fun incrementalDiscount(
        item: MenuItem,
        quantity: Int = 1,
    ): Double = projectedRewardResult(item, quantity).totalDiscount.roundMoney()

    fun eligibleMenuItems(template: LoyaltyRewardTemplate): List<MenuItem> {
        val allItems = _uiState.value.allItems
        return when (template.rule.type) {
            LoyaltyRewardRuleType.MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE -> allItems.filter { it.canBeOrdered }
            LoyaltyRewardRuleType.SPECIFIC_MENU_ITEM_PERCENTAGE,
            LoyaltyRewardRuleType.FREE_MENU_ITEM,
            LoyaltyRewardRuleType.BUY_X_GET_Y_FREE,
                -> {
                val targetId = template.targetMenuItemId ?: return emptyList()
                allItems.filter { it.id == targetId }
            }

            LoyaltyRewardRuleType.ACTIVITY_PERCENTAGE -> emptyList()
        }
    }

    fun rewardForAppliedReward(reward: AppliedReward): RewardPresentation =
        RewardPresentation.fromAppliedReward(reward)

    private fun startMenuObservationIfNeeded() {
        if (menuJob?.isActive == true) return

        menuJob = viewModelScope.launch {
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

    private fun projectedRewardResult(
        item: MenuItem,
        quantity: Int,
    ): RewardComputationResult = LoyaltyRewardEngine.evaluateRestaurant(
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

        return sections
            .map { section -> section.copy(items = section.items.sortedBy { it.sortOrder }) }
            .sortedWith(
                compareBy<MenuSection> {
                    val index = preferredOrder.indexOf(it.category.title)
                    if (index == -1) Int.MAX_VALUE else index
                }.thenBy { it.category.title },
            )
    }

    private fun Double.roundMoney(): Double = round(this * 100.0) / 100.0
}
