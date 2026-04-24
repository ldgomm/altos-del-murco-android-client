package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfile
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardsRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardComputationResult
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardWalletSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.ClearCartDraftUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.ObserveCartDraftUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.Order
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.SaveCartDraftUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.SubmitOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CheckoutUiState(
    val draft: OrderDraft = OrderDraft(),
    val isLoadingCart: Boolean = true,
    val isSubmitting: Boolean = false,
    val isLoadingRewards: Boolean = false,
    val rewardPreview: RewardComputationResult = RewardComputationResult.empty(
        RewardWalletSnapshot.empty(
            ""
        )
    ),
    val errorMessage: String? = null,
) {
    val subtotal: Double get() = draft.subtotal
    val discount: Double get() = rewardPreview.totalDiscount.coerceAtLeast(0.0)
    val total: Double get() = (subtotal - discount).coerceAtLeast(0.0)
    val canSubmit: Boolean get() = draft.canSubmit && !isSubmitting
}

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    observeCartDraftUseCase: ObserveCartDraftUseCase,
    private val saveCartDraftUseCase: SaveCartDraftUseCase,
    private val clearCartDraftUseCase: ClearCartDraftUseCase,
    private val submitOrderUseCase: SubmitOrderUseCase,
    private val loyaltyRewardsRepository: LoyaltyRewardsRepositoriable,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    private val _createdOrder = MutableSharedFlow<Order>()
    val createdOrder: SharedFlow<Order> = _createdOrder.asSharedFlow()

    private var currentNationalId: String = ""

    init {
        viewModelScope.launch {
            observeCartDraftUseCase.execute().collectLatest { draft ->
                _uiState.update {
                    it.copy(
                        draft = draft,
                        isLoadingCart = false,
                    )
                }
                refreshRewardPreview(draft)
            }
        }
    }

    fun syncProfile(profile: ClientProfile) {
        currentNationalId = profile.nationalId.filter { it.isDigit() }
        val current = _uiState.value.draft
        saveDraft(
            current.copy(
                nationalId = currentNationalId,
                clientName = profile.fullName,
            ),
        )
    }

    fun updateTableNumber(value: String) {
        saveDraft(_uiState.value.draft.copy(tableNumber = value.take(20)))
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun submit() {
        val draft = _uiState.value.draft

        if (!draft.canSubmit) {
            _uiState.update {
                it.copy(errorMessage = "Completa la mesa y asegúrate de tener productos en el carrito.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            runCatching {
                val preview = buildRewardPreview(draft)
                val finalOrder = draft
                    .toOrder(orderId = UUID.randomUUID().toString())
                    .withLoyalty(
                        appliedRewards = preview.appliedRewards,
                        discount = preview.totalDiscount,
                    )

                submitOrderUseCase.execute(finalOrder)
                clearCartDraftUseCase.execute()
                _createdOrder.emit(finalOrder)

                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        rewardPreview = RewardComputationResult.empty(
                            RewardWalletSnapshot.empty(currentNationalId),
                        ),
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = error.message ?: "No se pudo enviar el pedido.",
                    )
                }
            }
        }
    }

    private fun saveDraft(draft: OrderDraft) {
        viewModelScope.launch {
            runCatching {
                saveCartDraftUseCase.execute(draft)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "No se pudo actualizar el checkout.")
                }
            }
        }
    }

    private fun refreshRewardPreview(draft: OrderDraft) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingRewards = true) }
            runCatching {
                buildRewardPreview(draft)
            }.onSuccess { preview ->
                _uiState.update {
                    it.copy(
                        rewardPreview = preview,
                        isLoadingRewards = false,
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        rewardPreview = RewardComputationResult.empty(
                            RewardWalletSnapshot.empty(draft.nationalId.orEmpty()),
                        ),
                        isLoadingRewards = false,
                        errorMessage = error.message ?: "No se pudieron calcular beneficios.",
                    )
                }
            }
        }
    }

    private suspend fun buildRewardPreview(draft: OrderDraft): RewardComputationResult {
        val cleanNationalId = draft.nationalId?.trim().orEmpty()
        if (cleanNationalId.isEmpty() || draft.items.isEmpty()) {
            return RewardComputationResult.empty(RewardWalletSnapshot.empty(cleanNationalId))
        }

        val previewItems = draft.items.map {
            OrderItem(
                menuItemId = it.menuItem.id,
                name = it.menuItem.name,
                unitPrice = it.unitPrice,
                quantity = it.safeQuantity,
                notes = it.notes,
            )
        }

        return loyaltyRewardsRepository.previewRestaurantRewards(
            nationalId = cleanNationalId,
            items = previewItems,
        )
    }
}
