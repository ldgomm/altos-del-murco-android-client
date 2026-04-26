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
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderScheduleFormatter
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.SaveCartDraftUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.SubmitOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

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
    private var rewardPreviewJob: Job? = null

    init {
        viewModelScope.launch {
            observeCartDraftUseCase.execute().collectLatest { draft ->
                val refreshedDraft = if (!draft.isScheduledForLater && draft.scheduledAt.before(Date(System.currentTimeMillis() - 120_000L))) {
                    draft.copy(scheduledAt = Date())
                } else {
                    draft
                }

                _uiState.update {
                    it.copy(
                        draft = refreshedDraft,
                        isLoadingCart = false,
                    )
                }
                if (refreshedDraft != draft) saveDraft(refreshedDraft)
                refreshRewardPreview(refreshedDraft)
            }
        }
    }

    fun syncProfile(profile: ClientProfile) {
        currentNationalId = profile.nationalId.filter { it.isDigit() }
        val current = _uiState.value.draft
        val updated = current.copy(
            nationalId = currentNationalId,
            clientName = profile.fullName,
        )
        saveDraft(updated)
        refreshRewardPreview(updated)
    }

    fun updateTableNumber(value: String) {
        saveDraft(_uiState.value.draft.copy(tableNumber = value.take(30)))
    }

    fun updateScheduledAt(value: Date) {
        saveDraft(
            _uiState.value.draft.copy(
                scheduledAt = OrderScheduleFormatter.sanitizedScheduledAt(value),
            )
        )
    }

    fun scheduleForNow() {
        saveDraft(_uiState.value.draft.copy(scheduledAt = Date()))
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun submit() {
        val draft = _uiState.value.draft.normalizedForSubmit()

        if (!draft.canSubmit) {
            _uiState.update {
                it.copy(
                    errorMessage = if (draft.isScheduledForLater) {
                        "Agrega productos y confirma tu perfil. La mesa puede quedar por asignar para reservas."
                    } else {
                        "Completa la mesa y asegúrate de tener productos en el carrito."
                    }
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            try {
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
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
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
            try {
                saveCartDraftUseCase.execute(draft)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "No se pudo actualizar el checkout.")
                }
            }
        }
    }

    private fun refreshRewardPreview(draft: OrderDraft) {
        rewardPreviewJob?.cancel()
        rewardPreviewJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingRewards = true) }
            try {
                val preview = buildRewardPreview(draft)
                _uiState.update {
                    it.copy(
                        rewardPreview = preview,
                        isLoadingRewards = false,
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
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
        val cleanNationalId = draft.nationalId
            ?.filter(Char::isDigit)
            ?.takeIf { it.isNotEmpty() }
            ?: currentNationalId

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
