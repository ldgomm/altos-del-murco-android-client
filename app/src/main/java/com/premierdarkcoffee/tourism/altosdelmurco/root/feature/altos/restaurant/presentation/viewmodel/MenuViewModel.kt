package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardsRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentation
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentationFactory
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardWalletSnapshot
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

    fun onAppear(
        nationalId: String?,
    ) {
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

    fun rewardPresentation(
        item: MenuItem,
    ): RewardPresentation? = RewardPresentationFactory.menuPresentation(
        item = item,
        wallet = _uiState.value.walletSnapshot,
    )

    fun currentLevelTitle(): String = _uiState.value.walletSnapshot.currentLevel.title

    private fun refreshRewards(
        nationalId: String?,
    ) {
        val cleanNationalId = nationalId?.trim().orEmpty()
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
            }.onSuccess { wallet ->
                _uiState.update { it.copy(walletSnapshot = wallet) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        walletSnapshot = RewardWalletSnapshot.empty(cleanNationalId),
                        errorMessage = error.message ?: "Could not load loyalty wallet.",
                    )
                }
            }
        }
    }

    private fun sortSections(
        sections: List<MenuSection>,
    ): List<MenuSection> = sections
        .map { section ->
            section.copy(items = section.items.sortedBy { it.sortOrder })
        }
        .sortedWith(
            compareBy<MenuSection> { categoryRank(it.category.title) }
                .thenBy { it.category.title }
        )

    private fun categoryRank(title: String): Int = when (title.trim()) {
        "Entradas" -> 0
        "Sopas" -> 1
        "Platos Fuertes" -> 2
        "Extras" -> 3
        "Postres" -> 4
        "Bebidas" -> 5
        "Bebidas Alcohólicas" -> 6
        else -> Int.MAX_VALUE
    }
}
