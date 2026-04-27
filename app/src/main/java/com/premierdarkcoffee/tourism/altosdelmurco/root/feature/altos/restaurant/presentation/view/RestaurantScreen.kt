package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionState
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.Order
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.cart.CartScreen
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.cart.CheckoutScreen
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.menu.MenuItemDetailScreen
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.menu.MenuListScreen
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.order.OrderSuccessScreen
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.view.order.OrdersScreen
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel.CartViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel.CheckoutViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel.MenuViewModel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.presentation.viewmodel.OrdersViewModel

private sealed interface RestaurantDestination {
    data object Menu : RestaurantDestination
    data class Detail(val itemId: String) : RestaurantDestination
    data object Cart : RestaurantDestination
    data object Checkout : RestaurantDestination
    data object Orders : RestaurantDestination
    data class Success(val order: Order) : RestaurantDestination
}

@Composable
fun RestaurantScreen(
    sessionState: SessionState.Authenticated,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    menuViewModel: MenuViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
    checkoutViewModel: CheckoutViewModel = hiltViewModel(),
    ordersViewModel: OrdersViewModel = hiltViewModel(),
) {
    val menuState by menuViewModel.uiState.collectAsStateWithLifecycle()
    val cartState by cartViewModel.uiState.collectAsStateWithLifecycle()
    val checkoutState by checkoutViewModel.uiState.collectAsStateWithLifecycle()
    val ordersState by ordersViewModel.uiState.collectAsStateWithLifecycle()

    var destination: RestaurantDestination by remember { mutableStateOf(RestaurantDestination.Menu) }
    var pendingAddItemId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(sessionState.profile.id, sessionState.profile.updatedAt) {
        menuViewModel.onAppear(sessionState.profile.nationalId)
        cartViewModel.syncProfile(sessionState.profile)
        checkoutViewModel.syncProfile(sessionState.profile)
        ordersViewModel.syncProfile(sessionState.profile)
    }

    LaunchedEffect(Unit) {
        checkoutViewModel.createdOrder.collect { order ->
            destination = RestaurantDestination.Success(order)
        }
    }

    BackHandler(enabled = destination !is RestaurantDestination.Menu) {
        destination = RestaurantDestination.Menu
    }

    when (val current = destination) {
        RestaurantDestination.Menu -> {
            MenuListScreen(
                state = menuState,
                clientName = sessionState.profile.fullName,
                levelTitle = menuViewModel.currentLevelTitle(),
                cartItemsCount = cartState.totalItems,
                rewardProvider = { item -> menuViewModel.rewardPresentation(item) },
                eligibleItemsProvider = menuViewModel::eligibleMenuItems,
                onCategorySelected = menuViewModel::onCategorySelected,
                onOpenItem = { item -> destination = RestaurantDestination.Detail(item.id) },
                onOpenCart = { destination = RestaurantDestination.Cart },
                onOpenOrders = { destination = RestaurantDestination.Orders },
                onDismissError = menuViewModel::clearError,
                modifier = modifier,
            )
        }

        is RestaurantDestination.Detail -> {
            val item = menuState.itemById(current.itemId)
            if (item == null) {
                LaunchedEffect(current.itemId) { destination = RestaurantDestination.Menu }
            } else {
                MenuItemDetailScreen(
                    item = item,
                    rewardPresentationProvider = { menuItem, quantity ->
                        menuViewModel.rewardPresentation(menuItem, quantity)
                    },
                    displayedPriceProvider = { menuItem, quantity ->
                        menuViewModel.displayedPrice(menuItem, quantity)
                    },
                    incrementalDiscountProvider = { menuItem, quantity ->
                        menuViewModel.incrementalDiscount(menuItem, quantity)
                    },
                    onAddToCart = { menuItem, quantity, notes ->
                        if (pendingAddItemId != null) return@MenuItemDetailScreen

                        pendingAddItemId = menuItem.id

                        cartViewModel.addItem(menuItem, quantity, notes) { success ->
                            pendingAddItemId = null
                            if (success) {
                                destination = RestaurantDestination.Cart
                            }
                        }
                    },
                    onBack = { destination = RestaurantDestination.Menu },
                    modifier = modifier,
                )
            }
        }

        RestaurantDestination.Cart -> {
            CartScreen(
                state = cartState,
                onBack = { destination = RestaurantDestination.Menu },
                onCheckout = { destination = RestaurantDestination.Checkout },
                onIncrease = cartViewModel::increaseItem,
                onDecrease = cartViewModel::decreaseItem,
                onRemove = cartViewModel::removeItem,
                onClearCart = cartViewModel::clearCart,
                onDismissError = cartViewModel::clearError,
                modifier = modifier,
            )
        }

        RestaurantDestination.Checkout -> {
            CheckoutScreen(
                state = checkoutState,
                profile = sessionState.profile,
                onBack = { destination = RestaurantDestination.Cart },
                onTableNumberChanged = checkoutViewModel::updateTableNumber,
                onScheduledAtChanged = checkoutViewModel::updateScheduledAt,
                onScheduleNow = checkoutViewModel::scheduleForNow,
                onSubmit = checkoutViewModel::submit,
                onDismissError = checkoutViewModel::clearError,
                modifier = modifier,
            )
        }

        RestaurantDestination.Orders -> {
            OrdersScreen(
                state = ordersState,
                onBack = { destination = RestaurantDestination.Menu },
                onGroupingSelected = ordersViewModel::setGrouping,
                onSortSelected = ordersViewModel::setSortOption,
                onStatusSelected = ordersViewModel::setStatusFilter,
                onDismissError = ordersViewModel::clearError,
                modifier = modifier,
            )
        }

        is RestaurantDestination.Success -> {
            OrderSuccessScreen(
                order = current.order,
                onBackToRestaurant = { destination = RestaurantDestination.Menu },
                onOpenOrders = { destination = RestaurantDestination.Orders },
                modifier = modifier,
            )
        }
    }
}
