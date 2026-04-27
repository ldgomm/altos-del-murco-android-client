package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfile
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardsRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardComputationResult
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardWalletSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.CartItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.ClearCartDraftUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.ObserveCartDraftUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.SaveCartDraftUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.round

@HiltViewModel
class CartViewModel @Inject constructor(
    observeCartDraftUseCase: ObserveCartDraftUseCase,
    private val saveCartDraftUseCase: SaveCartDraftUseCase,
    private val clearCartDraftUseCase: ClearCartDraftUseCase,
    private val loyaltyRewardsRepository: LoyaltyRewardsRepositoriable,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private var currentNationalId: String = ""
    private var rewardPreviewJob: Job? = null

    init {
        viewModelScope.launch {
            observeCartDraftUseCase.execute().collectLatest { draft ->
                val draftNationalId = draft.nationalId?.filter(Char::isDigit).orEmpty()
                if (draftNationalId.isNotEmpty()) currentNationalId = draftNationalId

                _uiState.update {
                    it.copy(
                        draft = draft,
                        isLoading = false,
                    )
                }

                refreshRewardPreview(draft)
            }
        }
    }

    fun syncProfile(profile: ClientProfile) {
        val cleanNationalId = profile.nationalId.filter { it.isDigit() }
        currentNationalId = cleanNationalId

        val current = _uiState.value.draft

        val updated = current.copy(
            nationalId = cleanNationalId,
            clientName = profile.fullName,
            updatedAt = Date(),
        )

        _uiState.update {
            it.copy(draft = updated)
        }

        save(updated)
        refreshRewardPreview(updated)
    }

    fun addItem(
        menuItem: MenuItem,
        quantity: Int,
        notes: String?,
        onResult: (Boolean) -> Unit = {},
    ) {
        val safeQuantity = quantity.coerceAtLeast(1)

        if (!menuItem.canBeOrdered) {
            _uiState.update {
                it.copy(errorMessage = "${menuItem.name} está agotado o no disponible.")
            }
            onResult(false)
            return
        }

        val trimmedNotes = notes?.trim()?.takeIf { it.isNotEmpty() }
        val current = _uiState.value.draft

        val existingIndex = current.items.indexOfFirst {
            it.menuItem.id == menuItem.id &&
                    it.notes.orEmpty() == trimmedNotes.orEmpty()
        }

        val maxAllowed = menuItem.remainingQuantity.coerceAtLeast(1)

        val updatedItems = if (existingIndex >= 0) {
            current.items.mapIndexed { index, item ->
                if (index == existingIndex) {
                    val desired = item.safeQuantity + safeQuantity
                    item.copy(
                        menuItem = menuItem,
                        quantity = desired.coerceAtMost(maxAllowed),
                        notes = trimmedNotes,
                    )
                } else {
                    item
                }
            }
        } else {
            current.items + CartItem(
                menuItem = menuItem,
                quantity = safeQuantity.coerceAtMost(maxAllowed),
                notes = trimmedNotes,
            )
        }

        val updatedDraft = current.copy(
            items = updatedItems,
            updatedAt = Date(),
        )

        _uiState.update {
            it.copy(
                draft = updatedDraft,
                errorMessage = null,
                lastAddedItemName = menuItem.name,
            )
        }

        save(updatedDraft, onResult)
        refreshRewardPreview(updatedDraft)
    }

    fun increaseItem(cartItemId: String) {
        mutateItems { items ->
            items.map { item ->
                if (item.id == cartItemId) {
                    item.copy(
                        quantity = (item.safeQuantity + 1)
                            .coerceAtMost(item.menuItem.remainingQuantity.coerceAtLeast(1)),
                    )
                } else {
                    item
                }
            }
        }
    }

    fun decreaseItem(cartItemId: String) {
        mutateItems { items ->
            items.mapNotNull { item ->
                if (item.id == cartItemId) {
                    val newQuantity = item.safeQuantity - 1
                    if (newQuantity <= 0) null else item.copy(quantity = newQuantity)
                } else {
                    item
                }
            }
        }
    }

    fun removeItem(cartItemId: String) {
        mutateItems { items -> items.filterNot { it.id == cartItemId } }
    }

    fun updateItemNotes(cartItemId: String, notes: String?) {
        mutateItems { items ->
            items.map { item ->
                if (item.id == cartItemId) item.withNotes(notes) else item
            }
        }
    }

    fun updateTableNumber(value: String) {
        val cleaned = value.take(20)
        save(_uiState.value.draft.copy(tableNumber = cleaned, updatedAt = Date()))
    }

    fun dismissAddedMessage() {
        _uiState.update { it.copy(lastAddedItemName = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearCart() {
        viewModelScope.launch {
            try {
                clearCartDraftUseCase.execute()
                rewardPreviewJob?.cancel()
                _uiState.update {
                    it.copy(
                        rewardPreview = RewardComputationResult.empty(
                            RewardWalletSnapshot.empty(currentNationalId),
                        ),
                        isLoadingRewards = false,
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "No se pudo limpiar el carrito.")
                }
            }
        }
    }

    private fun mutateItems(transform: (List<CartItem>) -> List<CartItem>) {
        val current = _uiState.value.draft

        val updated = current.copy(
            items = transform(current.items),
            updatedAt = Date(),
        )

        _uiState.update {
            it.copy(draft = updated)
        }

        save(updated)
        refreshRewardPreview(updated)
    }

    private fun save(
        draft: OrderDraft,
        onResult: (Boolean) -> Unit = {},
    ) {
        viewModelScope.launch {
            try {
                saveCartDraftUseCase.execute(draft)
                onResult(true)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "No se pudo guardar el carrito.")
                }
                onResult(false)
            }
        }
    }

    private fun refreshRewardPreview(draft: OrderDraft) {
        rewardPreviewJob?.cancel()

        val cleanNationalId = draft.nationalId
            ?.filter(Char::isDigit)
            ?.takeIf { it.isNotEmpty() }
            ?: currentNationalId

        if (cleanNationalId.isEmpty() || draft.items.isEmpty()) {
            _uiState.update {
                it.copy(
                    isLoadingRewards = false,
                    rewardPreview = RewardComputationResult.empty(
                        RewardWalletSnapshot.empty(cleanNationalId),
                    ),
                )
            }
            return
        }

        rewardPreviewJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingRewards = true,
                    errorMessage = null,
                )
            }

            try {
                val preview = buildRewardPreview(
                    nationalId = cleanNationalId,
                    draft = draft,
                )

                _uiState.update {
                    it.copy(
                        rewardPreview = preview,
                        isLoadingRewards = false,
                        errorMessage = null,
                    )
                }
            } catch (_: CancellationException) {
                // Expected when the cart changes quickly. Do not show this as a UI error.
            } catch (error: Throwable) {
                _uiState.update {
                    it.copy(
                        rewardPreview = RewardComputationResult.empty(
                            RewardWalletSnapshot.empty(cleanNationalId),
                        ),
                        isLoadingRewards = false,
                        errorMessage = error.message ?: "No se pudieron calcular beneficios.",
                    )
                }
            }
        }
    }

    private suspend fun buildRewardPreview(
        nationalId: String,
        draft: OrderDraft,
    ): RewardComputationResult {
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
            nationalId = nationalId,
            items = previewItems,
        )
    }
}

fun Double.roundMoney(): Double = round(this * 100.0) / 100.0

