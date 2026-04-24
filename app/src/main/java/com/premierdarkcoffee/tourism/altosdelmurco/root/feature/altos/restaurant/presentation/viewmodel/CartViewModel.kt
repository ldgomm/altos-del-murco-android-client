package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfile
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.CartItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.ClearCartDraftUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.ObserveCartDraftUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.SaveCartDraftUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CartUiState(
    val draft: OrderDraft = OrderDraft(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val lastAddedItemName: String? = null,
) {
    val items: List<CartItem> get() = draft.items
    val totalItems: Int get() = draft.totalItems
    val subtotal: Double get() = draft.subtotal
    val isEmpty: Boolean get() = draft.isEmpty
    val canCheckout: Boolean get() = !draft.isEmpty
}

@HiltViewModel
class CartViewModel @Inject constructor(
    observeCartDraftUseCase: ObserveCartDraftUseCase,
    private val saveCartDraftUseCase: SaveCartDraftUseCase,
    private val clearCartDraftUseCase: ClearCartDraftUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeCartDraftUseCase.execute().collectLatest { draft ->
                _uiState.update {
                    it.copy(
                        draft = draft,
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun syncProfile(profile: ClientProfile) {
        val current = _uiState.value.draft
        val cleanNationalId = profile.nationalId.filter { it.isDigit() }
        val updated = current.copy(
            nationalId = cleanNationalId,
            clientName = profile.fullName,
            updatedAt = Date(),
        )
        save(updated)
    }

    fun addItem(
        menuItem: MenuItem,
        quantity: Int,
        notes: String?,
    ) {
        val safeQuantity = quantity.coerceAtLeast(1)

        if (!menuItem.canBeOrdered) {
            _uiState.update {
                it.copy(errorMessage = "${menuItem.name} está agotado o no disponible.")
            }
            return
        }

        val trimmedNotes = notes?.trim()?.takeIf { it.isNotEmpty() }
        val current = _uiState.value.draft
        val existingIndex = current.items.indexOfFirst {
            it.menuItem.id == menuItem.id && it.notes.orEmpty() == trimmedNotes.orEmpty()
        }

        val updatedItems = if (existingIndex >= 0) {
            current.items.mapIndexed { index, item ->
                if (index == existingIndex) {
                    val desired = item.safeQuantity + safeQuantity
                    val maxAllowed = menuItem.remainingQuantity.coerceAtLeast(1)
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
                quantity = safeQuantity.coerceAtMost(menuItem.remainingQuantity.coerceAtLeast(1)),
                notes = trimmedNotes,
            )
        }

        save(
            current.copy(
                items = updatedItems,
                updatedAt = Date(),
            ),
        )

        _uiState.update {
            it.copy(
                errorMessage = null,
                lastAddedItemName = menuItem.name,
            )
        }
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
            runCatching {
                clearCartDraftUseCase.execute()
            }.onFailure { error ->
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
        save(updated)
    }

    private fun save(draft: OrderDraft) {
        viewModelScope.launch {
            runCatching {
                saveCartDraftUseCase.execute(draft)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "No se pudo guardar el carrito.")
                }
            }
        }
    }
}
