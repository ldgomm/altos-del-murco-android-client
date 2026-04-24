package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureActivityCatalogItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureActivityType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureAvailabilitySlot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureBookingRequest
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureDateHelper
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureFeaturedPackage
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventurePlanner
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventurePricingEngine
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureReservationItemDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.CreateAdventureBookingUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.GetAdventureAvailabilityUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ObserveAdventureCatalogUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationEventType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationFoodDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationFoodItemDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationServingMoment
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.adventureRoundMoney
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ClientProfile
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardEngine
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardsRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardActivityLine
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardComputationResult
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardMenuLine
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentation
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardPresentationFactory
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardWalletSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuSection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class AdventureComboBuilderViewModel @Inject constructor(
    private val getAvailabilityUseCase: GetAdventureAvailabilityUseCase,
    private val createBookingUseCase: CreateAdventureBookingUseCase,
    private val observeAdventureCatalogUseCase: ObserveAdventureCatalogUseCase,
    private val loyaltyRewardsRepository: LoyaltyRewardsRepositoriable,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdventureComboBuilderUiState())
    val uiState: StateFlow<AdventureComboBuilderUiState> = _uiState.asStateFlow()

    private val rewardPreviewRequests = MutableStateFlow<RewardPreviewInput?>(null)
    private var catalogJob: Job? = null
    private var availabilityJob: Job? = null
    private var rewardPreviewJob: Job? = null

    init {
        startRewardPreviewLoop()
    }

    fun onAppear(profile: ClientProfile? = null) {
        profile?.let(::syncProfile)
        startCatalogObservationIfNeeded()
    }

    fun onDisappear() {
        catalogJob?.cancel()
        catalogJob = null
    }

    fun syncProfile(profile: ClientProfile) {
        _uiState.update {
            it.copy(
                clientName = profile.fullName,
                whatsappNumber = profile.phoneNumber,
                nationalId = profile.nationalId.filter(Char::isDigit),
            )
        }
        requestRewardPreview()
    }

    fun setDate(date: Date) {
        val startOfDay = AdventureDateHelper.startOfDay(date)
        _uiState.update {
            it.copy(
                selectedDate = startOfDay,
                selectedSlot = null,
            )
        }
        refreshAvailability()
    }

    fun setGuestCount(value: Int) = updateState { copy(guestCount = value.coerceIn(1, 300)) }
    fun setEventType(value: ReservationEventType) = updateState { copy(eventType = value) }
    fun setCustomEventTitle(value: String) = updateState { copy(customEventTitle = value) }
    fun setEventNotes(value: String) = updateState { copy(eventNotes = value) }
    fun setFoodServingMoment(value: ReservationServingMoment) =
        updateState { copy(foodServingMoment = value) }

    fun setFoodServingTime(value: Date) = updateState { copy(foodServingTime = value) }
    fun setFoodNotes(value: String) = updateState { copy(foodNotes = value) }
    fun setNotes(value: String) = updateState { copy(notes = value) }
    fun setClientName(value: String) = updateState { copy(clientName = value) }
    fun setWhatsapp(value: String) = updateState { copy(whatsappNumber = value) }

    fun setNationalId(value: String) {
        updateState { copy(nationalId = value.filter(Char::isDigit)) }
        requestRewardPreview()
    }

    fun prepareCustomDraftIfNeeded() {
        val state = _uiState.value
        if (state.items.isEmpty() && state.foodItems.isEmpty()) {
            val firstActivity = state.catalog.activeActivitiesSorted.firstOrNull()
            if (firstActivity != null) {
                replaceItems(listOf(firstActivity.defaultDraft), 0.0)
            } else {
                refreshAvailability()
            }
        }
    }

    fun replaceItems(
        items: List<AdventureReservationItemDraft>,
        packageDiscountAmount: Double,
    ) {
        _uiState.update {
            it.copy(
                items = items,
                packageDiscountAmount = packageDiscountAmount.coerceAtLeast(0.0),
                selectedSlot = null,
                createdBooking = null,
                successMessage = null,
                errorMessage = null,
            )
        }
        requestRewardPreview()
        refreshAvailability()
    }

    fun replacePackage(
        packageModel: AdventureFeaturedPackage,
        menuSections: List<MenuSection>,
    ) {
        val foodItems = packageModel.foodItems.mapNotNull { food ->
            val menuItem =
                menuSections.flatMap { it.items }.firstOrNull { it.id == food.menuItemId }
                    ?: return@mapNotNull null
            ReservationFoodItemDraft(menuItem = menuItem, quantity = food.quantity)
        }

        _uiState.update {
            it.copy(
                items = packageModel.items,
                foodItems = foodItems,
                packageDiscountAmount = packageModel.packageDiscountAmount.coerceAtLeast(0.0),
                selectedSlot = null,
                createdBooking = null,
                successMessage = null,
                errorMessage = null,
            )
        }
        requestRewardPreview()
        refreshAvailability()
    }

    val availableActivitiesToAdd: List<AdventureActivityCatalogItem>
        get() {
            val selected = _uiState.value.items.map { it.activity }.toSet()
            return _uiState.value.catalog.activeActivitiesSorted.filterNot { it.activityType in selected }
        }

    fun addItem(activity: AdventureActivityType) {
        val state = _uiState.value
        if (state.items.any { it.activity == activity }) return
        val draft = AdventureActivityType.defaultDraft(activity, state.catalog)
        updateItems(state.items + draft)
    }

    fun updateItem(updated: AdventureReservationItemDraft) {
        val current = _uiState.value.items
        updateItems(current.map { if (it.id == updated.id) updated else it })
    }

    fun removeItem(itemId: String) {
        updateItems(_uiState.value.items.filterNot { it.id == itemId })
    }

    fun removeItemAt(index: Int) {
        val current = _uiState.value.items.toMutableList()
        if (index !in current.indices) return
        current.removeAt(index)
        updateItems(current)
    }

    fun moveItem(from: Int, to: Int) {
        val current = _uiState.value.items.toMutableList()
        if (from !in current.indices || to !in 0..current.size) return
        val item = current.removeAt(from)
        current.add(if (to > from) to - 1 else to, item)
        updateItems(current)
    }

    fun addFoodItem(
        menuItem: MenuItem,
        quantity: Int,
        notes: String?,
        selectedDate: Date = _uiState.value.selectedDate,
    ) {
        if (AdventureDateHelper.sameDay(selectedDate, Date()) && !menuItem.canBeOrdered) {
            presentError("Por hoy, ${menuItem.name} está agotado y no se puede pedir. Elige otro día para reservarlo.")
            return
        }

        val cleanNotes = notes?.trim()?.takeIf { it.isNotEmpty() }
        val current = _uiState.value.foodItems.toMutableList()
        val index =
            current.indexOfFirst { it.menuItemId == menuItem.id && it.notes.orEmpty() == cleanNotes.orEmpty() }
        if (index >= 0) {
            val existing = current[index]
            current[index] = existing.copy(quantity = existing.quantity + quantity.coerceAtLeast(1))
        } else {
            current.add(
                ReservationFoodItemDraft(
                    menuItem = menuItem,
                    quantity = quantity,
                    notes = cleanNotes
                )
            )
        }
        updateFoodItems(current)
    }

    fun updateFoodItem(updated: ReservationFoodItemDraft) {
        val current = _uiState.value.foodItems.map {
            if (it.id == updated.id) updated.copy(
                quantity = updated.quantity.coerceAtLeast(1)
            ) else it
        }
        updateFoodItems(current)
    }

    fun increaseFoodQuantity(itemId: String) {
        updateFoodItems(_uiState.value.foodItems.map { if (it.id == itemId) it.copy(quantity = it.quantity + 1) else it })
    }

    fun decreaseFoodQuantity(itemId: String) {
        updateFoodItems(_uiState.value.foodItems.map { item ->
            if (item.id == itemId) item.copy(quantity = (item.quantity - 1).coerceAtLeast(1)) else item
        })
    }

    fun removeFoodItem(itemId: String) {
        updateFoodItems(_uiState.value.foodItems.filterNot { it.id == itemId })
    }

    fun selectSlot(slot: AdventureAvailabilitySlot) {
        _uiState.update { it.copy(selectedSlot = slot) }
    }

    fun submit(clientId: String?) {
        val state = _uiState.value
        val selectedSlot = state.selectedSlot
        val validationMessage = validateBeforeSubmit(state)
        if (validationMessage != null) {
            presentError(validationMessage)
            return
        }
        if (selectedSlot == null) {
            presentError("Selecciona un horario disponible antes de confirmar.")
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSubmitting = true,
                    errorMessage = null,
                    successMessage = null
                )
            }
            val request = AdventureBookingRequest(
                clientId = clientId,
                clientName = state.clientName.trim(),
                whatsappNumber = state.whatsappNumber.trim(),
                nationalId = state.nationalId.filter(Char::isDigit),
                date = state.selectedDate,
                selectedStartAt = selectedSlot.startAt,
                guestCount = state.guestCount.coerceAtLeast(1),
                eventType = state.eventType,
                customEventTitle = state.customEventTitle.trim().takeIf { it.isNotEmpty() },
                eventNotes = state.eventNotes.trim().takeIf { it.isNotEmpty() },
                items = state.items,
                foodReservation = buildFoodDraft(state),
                packageDiscountAmount = state.packageDiscountAmount.coerceAtLeast(0.0),
                loyaltyDiscountAmount = state.rewardPreview.totalDiscount,
                appliedRewards = state.rewardPreview.appliedRewards,
                notes = state.notes.trim().takeIf { it.isNotEmpty() },
            )

            runCatching {
                createBookingUseCase.execute(request)
            }.onSuccess { booking ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        createdBooking = booking,
                        successMessage = "Reserva enviada. Te confirmaremos pronto.",
                        selectedSlot = null,
                    )
                }
                refreshAvailability()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = error.message ?: "No se pudo crear la reserva.",
                    )
                }
            }
        }
    }

    fun dismissMessage() = updateState { copy(errorMessage = null, successMessage = null) }

    fun presentError(message: String) {
        _uiState.update { it.copy(errorMessage = message, successMessage = null) }
    }

    fun config(activity: AdventureActivityType): AdventureActivityCatalogItem? =
        _uiState.value.catalog.activity(activity)

    val estimatedAdventureSubtotal: Double
        get() = AdventurePricingEngine.estimatedSubtotal(
            _uiState.value.items,
            _uiState.value.catalog
        )

    val estimatedFoodSubtotal: Double
        get() = _uiState.value.foodItems.sumOf { it.subtotal }.adventureRoundMoney()

    val estimatedDiscountAmount: Double
        get() {
            val state = _uiState.value
            val activityDiscount =
                AdventurePricingEngine.estimatedDiscountAmount(state.items, state.catalog)
            return (activityDiscount + state.packageDiscountAmount + state.rewardPreview.totalDiscount).adventureRoundMoney()
        }

    val estimatedTotal: Double
        get() {
            val state = _uiState.value
            val plan = AdventurePlanner.buildPlan(
                day = state.selectedDate,
                startAt = AdventureDateHelper.dateOn(state.selectedDate, 7, 0),
                items = state.items,
                foodReservation = buildFoodDraft(state),
                packageDiscountAmount = state.packageDiscountAmount,
                catalog = state.catalog,
            )
            val base = plan?.totalAmount
                ?: (estimatedAdventureSubtotal + estimatedFoodSubtotal - state.packageDiscountAmount).coerceAtLeast(
                    0.0
                )
            return (base - state.rewardPreview.totalDiscount).coerceAtLeast(0.0)
                .adventureRoundMoney()
        }

    val activeRewardPresentations: List<RewardPresentation>
        get() = _uiState.value.rewardPreview.appliedRewards.map {
            RewardPresentation.fromAppliedReward(
                it
            )
        }

    fun effectiveTotal(slot: AdventureAvailabilitySlot): Double =
        (slot.totalAmount - _uiState.value.rewardPreview.totalDiscount).coerceAtLeast(0.0)
            .adventureRoundMoney()

    fun baseAdventureSubtotal(item: AdventureReservationItemDraft): Double =
        _uiState.value.catalog.activity(item.activity)?.let { config ->
            AdventurePricingEngine.lineBaseSubtotal(item, config)
        } ?: 0.0

    fun displayedAdventureSubtotal(item: AdventureReservationItemDraft): Double {
        val raw = AdventurePricingEngine.subtotal(item, _uiState.value.catalog)
        val reward = rewardAmountForActivity(item.activity)
        return (raw - reward).coerceAtLeast(0.0).adventureRoundMoney()
    }

    fun displayedFoodSubtotal(item: ReservationFoodItemDraft): Double =
        (item.subtotal - rewardAmount(item)).coerceAtLeast(0.0).adventureRoundMoney()

    fun rewardAmount(item: ReservationFoodItemDraft): Double =
        _uiState.value.rewardPreview.appliedRewards
            .filter { reward -> reward.affectedMenuItemIds.contains(item.menuItemId) }
            .sumOf { it.amount }
            .adventureRoundMoney()

    fun appliedRewardPresentation(item: AdventureReservationItemDraft): RewardPresentation? =
        _uiState.value.rewardPreview.appliedRewards
            .firstOrNull { reward -> reward.affectedActivityIds.contains(item.activity.rawValue) }
            ?.let(RewardPresentation::fromAppliedReward)

    fun appliedRewardPresentation(item: ReservationFoodItemDraft): RewardPresentation? =
        _uiState.value.rewardPreview.appliedRewards
            .firstOrNull { reward -> reward.affectedMenuItemIds.contains(item.menuItemId) }
            ?.let(RewardPresentation::fromAppliedReward)

    fun catalogRewardPresentation(activity: AdventureActivityCatalogItem): RewardPresentation? =
        RewardPresentationFactory.activityPresentation(
            activity = activity,
            wallet = _uiState.value.rewardPreview.walletSnapshot,
        )

    fun packageRewardPresentation(
        packageModel: AdventureFeaturedPackage,
        menuSections: List<MenuSection>,
    ): RewardPresentation? = RewardPresentationFactory.packagePresentation(
        packageModel = packageModel,
        catalog = _uiState.value.catalog,
        menuItemsById = menuSections.flatMap { it.items }.associateBy { it.id },
        wallet = _uiState.value.rewardPreview.walletSnapshot,
    )

    fun foodPickerRewardPresentation(item: MenuItem, quantity: Int): RewardPresentation? {
        val projected = projectedRewardResult(item, quantity)
        projected.appliedRewards.firstOrNull { reward -> reward.affectedMenuItemIds.contains(item.id) }
            ?.let { return RewardPresentation.fromAppliedReward(it) }
        return RewardPresentationFactory.adventureMenuPresentation(item, projected.walletSnapshot)
    }

    fun foodPickerDisplayedPrice(item: MenuItem, quantity: Int): Double {
        val subtotal = item.finalPrice * quantity.coerceAtLeast(1)
        return (subtotal - foodPickerIncrementalDiscount(item, quantity)).coerceAtLeast(0.0)
            .adventureRoundMoney()
    }

    fun foodPickerIncrementalDiscount(item: MenuItem, quantity: Int): Double =
        projectedRewardResult(item, quantity).totalDiscount.adventureRoundMoney()

    private fun updateItems(items: List<AdventureReservationItemDraft>) {
        val correctedDiscount = bestMatchingPackageDiscount(items)
        _uiState.update {
            it.copy(
                items = items,
                packageDiscountAmount = correctedDiscount,
                selectedSlot = null,
            )
        }
        requestRewardPreview()
        refreshAvailability()
    }

    private fun updateFoodItems(foodItems: List<ReservationFoodItemDraft>) {
        _uiState.update {
            it.copy(
                foodItems = foodItems,
                selectedSlot = null,
            )
        }
        requestRewardPreview()
        refreshAvailability()
    }

    private fun bestMatchingPackageDiscount(items: List<AdventureReservationItemDraft>): Double {
        if (items.size <= 1) return 0.0
        val state = _uiState.value
        val activityKey = items
            .map { keyForActivityPackageMatch(it) }
            .sorted()
        return state.catalog.activePackagesSorted
            .filter { it.items.size > 1 }
            .firstOrNull { packageModel ->
                packageModel.items.map { keyForActivityPackageMatch(it) }.sorted() == activityKey
            }
            ?.packageDiscountAmount
            ?.coerceAtLeast(0.0)
            ?: 0.0
    }

    private fun keyForActivityPackageMatch(item: AdventureReservationItemDraft): String =
        listOf(
            item.activity.rawValue,
            item.durationMinutes,
            item.peopleCount,
            item.vehicleCount,
            item.offRoadRiderCount,
            item.nights,
        ).joinToString("|")

    private fun refreshAvailability() {
        val state = _uiState.value
        val hasFood = state.foodItems.isNotEmpty()
        if (state.items.isEmpty() && !hasFood) {
            availabilityJob?.cancel()
            _uiState.update {
                it.copy(
                    availableSlots = emptyList(),
                    selectedSlot = null,
                    isLoadingAvailability = false
                )
            }
            return
        }

        availabilityJob?.cancel()
        availabilityJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAvailability = true, errorMessage = null) }
            delay(120)
            runCatching {
                getAvailabilityUseCase.execute(
                    date = _uiState.value.selectedDate,
                    items = _uiState.value.items,
                    foodReservation = buildFoodDraft(_uiState.value),
                    packageDiscountAmount = _uiState.value.packageDiscountAmount,
                )
            }.onSuccess { slots ->
                _uiState.update { current ->
                    val selected = current.selectedSlot?.let { previous ->
                        slots.firstOrNull { it.startAt == previous.startAt && it.endAt == previous.endAt }
                    }
                    current.copy(
                        availableSlots = slots,
                        selectedSlot = selected,
                        isLoadingAvailability = false,
                        errorMessage = null,
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoadingAvailability = false,
                        availableSlots = emptyList(),
                        selectedSlot = null,
                        errorMessage = error.message ?: "No se pudo verificar disponibilidad.",
                    )
                }
            }
        }
    }

    private fun startCatalogObservationIfNeeded() {
        if (catalogJob?.isActive == true) return
        catalogJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCatalog = true, errorMessage = null) }
            observeAdventureCatalogUseCase.execute()
                .catch { error ->
                    if (error is CancellationException) throw error
                    _uiState.update {
                        it.copy(
                            isLoadingCatalog = false,
                            errorMessage = error.message ?: "No se pudo cargar aventura.",
                        )
                    }
                }
                .collectLatest { catalog ->
                    _uiState.update { state ->
                        val validItems =
                            state.items.filter { catalog.activity(it.activity)?.isActive == true }
                        state.copy(
                            catalog = catalog,
                            items = validItems,
                            packageDiscountAmount = if (validItems.size > 1) state.packageDiscountAmount else 0.0,
                            isLoadingCatalog = false,
                            errorMessage = null,
                        )
                    }
                    requestRewardPreview()
                    refreshAvailability()
                }
        }
    }

    private fun startRewardPreviewLoop() {
        rewardPreviewJob?.cancel()
        rewardPreviewJob = viewModelScope.launch {
            rewardPreviewRequests
                .filter { it != null }
                .map { requireNotNull(it) }
                .distinctUntilChanged()
                .debounce(180)
                .collectLatest { input ->
                    if (input.nationalId.isBlank()) {
                        _uiState.update {
                            it.copy(
                                rewardPreview = RewardComputationResult.empty(
                                    RewardWalletSnapshot.empty("")
                                )
                            )
                        }
                        return@collectLatest
                    }

                    _uiState.update { it.copy(isLoadingRewards = true) }
                    runCatching {
                        loyaltyRewardsRepository.previewAdventureRewards(
                            nationalId = input.nationalId,
                            activityItems = input.activityItems,
                            foodItems = input.foodItems,
                            catalog = input.catalog,
                        )
                    }.onSuccess { result ->
                        _uiState.update {
                            it.copy(
                                rewardPreview = result,
                                isLoadingRewards = false,
                            )
                        }
                    }.onFailure { error ->
                        if (error is CancellationException) throw error
                        _uiState.update {
                            it.copy(
                                rewardPreview = RewardComputationResult.empty(
                                    RewardWalletSnapshot.empty(
                                        input.nationalId
                                    )
                                ),
                                isLoadingRewards = false,
                            )
                        }
                    }
                }
        }
    }

    private fun requestRewardPreview() {
        val state = _uiState.value
        rewardPreviewRequests.value = RewardPreviewInput(
            nationalId = state.nationalId.filter(Char::isDigit),
            activityItems = state.items,
            foodItems = state.foodItems,
            catalog = state.catalog,
        )
    }

    private fun projectedRewardResult(item: MenuItem, quantity: Int): RewardComputationResult {
        val state = _uiState.value
        val wallet = state.rewardPreview.walletSnapshot
        val projectedItems =
            state.foodItems + ReservationFoodItemDraft(menuItem = item, quantity = quantity)

        val activityLines = state.items.mapNotNull { draft ->
            val config = state.catalog.activity(draft.activity) ?: return@mapNotNull null
            RewardActivityLine(
                activityId = draft.activity.rawValue,
                title = config.title,
                linePrice = AdventurePricingEngine.subtotal(draft, state.catalog),
            )
        }

        val foodLines = projectedItems.map { food ->
            RewardMenuLine(
                menuItemId = food.menuItemId,
                name = food.name,
                unitPrice = food.unitPrice,
                quantity = food.quantity,
            )
        }

        return LoyaltyRewardEngine.evaluateAdventure(
            templates = wallet.availableTemplates,
            wallet = wallet,
            activityLines = activityLines,
            foodLines = foodLines,
        )
    }

    private fun rewardAmountForActivity(activity: AdventureActivityType): Double =
        _uiState.value.rewardPreview.appliedRewards
            .filter { reward -> reward.affectedActivityIds.contains(activity.rawValue) }
            .sumOf { it.amount }
            .adventureRoundMoney()

    private fun buildFoodDraft(state: AdventureComboBuilderUiState): ReservationFoodDraft? {
        if (state.foodItems.isEmpty()) return null
        return ReservationFoodDraft(
            items = state.foodItems,
            servingMoment = state.foodServingMoment,
            servingTime = if (state.foodServingMoment == ReservationServingMoment.SPECIFIC_TIME) state.foodServingTime else null,
            notes = state.foodNotes.trim().takeIf { it.isNotEmpty() },
        )
    }

    private fun validateBeforeSubmit(state: AdventureComboBuilderUiState): String? {
        if (state.items.isEmpty() && state.foodItems.isEmpty()) return "Agrega al menos una actividad o comida."
        if (state.clientName.trim().isEmpty()) return "Tu perfil no tiene nombre registrado."
        if (state.whatsappNumber.trim().isEmpty()) return "Tu perfil no tiene WhatsApp registrado."
        if (state.nationalId.filter(Char::isDigit)
                .isEmpty()
        ) return "Tu perfil no tiene cédula registrada."
        if (state.eventType == ReservationEventType.CUSTOM && state.customEventTitle.trim()
                .isEmpty()
        ) {
            return "Indica el nombre del evento personalizado."
        }
        return null
    }

    private inline fun updateState(transform: AdventureComboBuilderUiState.() -> AdventureComboBuilderUiState) {
        _uiState.update(transform)
    }
}
