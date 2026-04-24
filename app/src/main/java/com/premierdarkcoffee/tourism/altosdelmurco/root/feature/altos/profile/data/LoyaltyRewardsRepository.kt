package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureActivityCatalogItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureActivityType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureCatalogSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureReservationItemDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationFoodItemDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.AppliedReward
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyLevel
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardEngine
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardReferenceType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardRule
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardRuleType
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardScope
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardTemplate
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardTriggerMode
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardsRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyWalletEvent
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyWalletEventStatus
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardActivityLine
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardComputationResult
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardMenuLine
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.RewardWalletSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderItem
import com.premierdarkcoffee.tourism.altosdelmurco.util.constant.FirestoreCollections
import com.premierdarkcoffee.tourism.altosdelmurco.util.database.awaitResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.round

@Singleton
class LoyaltyRewardsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : LoyaltyRewardsRepositoriable {

    override suspend fun loadWalletSnapshot(nationalId: String): RewardWalletSnapshot {
        val cleanNationalId = nationalId.cleanNationalId()
        if (cleanNationalId.isEmpty()) return RewardWalletSnapshot.empty("")

        val templates = fetchTemplates()
        val totals = computeTotals(cleanNationalId)
        val walletEvents = fetchWalletEvents(cleanNationalId)
        val currentLevel = LoyaltyLevel.fromTotalSpent(totals.totalSpent)

        val eligibleTemplates = templates
            .filter { template ->
                template.isActive &&
                        !template.isExpired &&
                        template.triggerMode == LoyaltyRewardTriggerMode.AUTOMATIC &&
                        template.isEligible(currentLevel) &&
                        usageCount(
                            template.id,
                            walletEvents
                        ) < template.maxUsesPerClient.coerceAtLeast(1)
            }
            .sortedWith(compareBy<LoyaltyRewardTemplate> { it.priority }.thenBy { it.title })

        return RewardWalletSnapshot(
            nationalId = cleanNationalId,
            currentLevel = currentLevel,
            totalSpent = totals.totalSpent.roundMoney(),
            points = totals.totalSpent.toInt(),
            availableTemplates = eligibleTemplates,
            reservedEvents = walletEvents.filter { it.status == LoyaltyWalletEventStatus.RESERVED },
            consumedEvents = walletEvents.filter { it.status == LoyaltyWalletEventStatus.CONSUMED },
            releasedEvents = walletEvents.filter { it.status == LoyaltyWalletEventStatus.RELEASED },
        )
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    override fun observeWalletSnapshot(nationalId: String): Flow<RewardWalletSnapshot> =
        callbackFlow {
            val cleanNationalId = nationalId.cleanNationalId()
            if (cleanNationalId.isEmpty()) {
                trySend(RewardWalletSnapshot.empty(""))
                close()
                return@callbackFlow
            }

            val refreshRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 32)
            val registrations = mutableListOf<ListenerRegistration>()

            val loaderJob: Job = launch {
                refreshRequests
                    .onStart { emit(Unit) }
                    .debounce(160)
                    .mapLatest { loadWalletSnapshot(cleanNationalId) }
                    .catch { error ->
                        if (error is CancellationException) throw error
                        close(error)
                    }
                    .collect { wallet ->
                        trySend(wallet).isSuccess
                    }
            }

            fun requestRefresh() {
                refreshRequests.tryEmit(Unit)
            }

            registrations += firestore
                .collection(FirestoreCollections.CLIENT_LOYALTY_WALLETS)
                .document(cleanNationalId)
                .addSnapshotListener { _, error ->
                    if (error != null) close(error) else requestRefresh()
                }

            registrations += firestore
                .collection(FirestoreCollections.LOYALTY_REWARD_TEMPLATES)
                .addSnapshotListener { _, error ->
                    if (error != null) close(error) else requestRefresh()
                }

            registrations += firestore
                .collection(FirestoreCollections.RESTAURANT_ORDERS)
                .whereEqualTo("nationalId", cleanNationalId)
                .addSnapshotListener { _, error ->
                    if (error != null) close(error) else requestRefresh()
                }

            registrations += firestore
                .collection(FirestoreCollections.ADVENTURE_BOOKINGS)
                .whereEqualTo("nationalId", cleanNationalId)
                .addSnapshotListener { _, error ->
                    if (error != null) close(error) else requestRefresh()
                }

            requestRefresh()

            awaitClose {
                registrations.forEach { it.remove() }
                registrations.clear()
                loaderJob.cancel()
            }
        }

    override suspend fun previewRestaurantRewards(
        nationalId: String,
        items: List<OrderItem>,
    ): RewardComputationResult {
        val wallet = loadWalletSnapshot(nationalId)
        val lines = items.map {
            RewardMenuLine(
                menuItemId = it.menuItemId,
                name = it.name,
                unitPrice = it.unitPrice,
                quantity = it.quantity,
            )
        }

        return LoyaltyRewardEngine.evaluateRestaurant(
            templates = wallet.availableTemplates,
            wallet = wallet,
            menuLines = lines,
        )
    }

    override suspend fun previewAdventureRewards(
        nationalId: String,
        activityItems: List<AdventureReservationItemDraft>,
        foodItems: List<ReservationFoodItemDraft>,
        catalog: AdventureCatalogSnapshot,
    ): RewardComputationResult {
        val wallet = loadWalletSnapshot(nationalId)

        val activityLines = activityItems.mapNotNull { item ->
            val activity = catalog.activity(item.activity) ?: return@mapNotNull null
            RewardActivityLine(
                activityId = activity.id,
                title = activity.title,
                linePrice = adventureSubtotalFor(item, activity),
            )
        }

        val foodLines = foodItems.map {
            RewardMenuLine(
                menuItemId = it.menuItemId,
                name = it.name,
                unitPrice = it.unitPrice,
                quantity = it.quantity,
            )
        }

        return LoyaltyRewardEngine.evaluateAdventure(
            templates = wallet.availableTemplates,
            wallet = wallet,
            activityLines = activityLines,
            foodLines = foodLines,
        )
    }

    override suspend fun reserveRewards(
        nationalId: String,
        referenceType: LoyaltyRewardReferenceType,
        referenceId: String,
        appliedRewards: List<AppliedReward>,
    ) {
        val cleanNationalId = nationalId.cleanNationalId()
        if (cleanNationalId.isEmpty() || referenceId.isBlank() || appliedRewards.isEmpty()) return

        val walletRef = firestore
            .collection(FirestoreCollections.CLIENT_LOYALTY_WALLETS)
            .document(cleanNationalId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(walletRef)
            val events = snapshot.walletEvents().toMutableList()
            val now = Date()

            appliedRewards.forEach { reward ->
                val templateRef = firestore
                    .collection(FirestoreCollections.LOYALTY_REWARD_TEMPLATES)
                    .document(reward.templateId)
                val templateSnapshot = transaction.get(templateRef)
                val template = templateSnapshot.toLoyaltyRewardTemplateOrNull()
                    ?: throw IllegalStateException("El premio ${reward.title} ya no existe.")

                if (!template.isActive || template.isExpired) {
                    throw IllegalStateException("El premio ${template.title} ya no está disponible.")
                }

                if (usageCount(template.id, events) >= template.maxUsesPerClient.coerceAtLeast(1)) {
                    throw IllegalStateException("El premio ${template.title} ya fue usado.")
                }

                events += LoyaltyWalletEvent(
                    id = reward.id,
                    templateId = reward.templateId,
                    templateTitle = reward.title,
                    referenceType = referenceType,
                    referenceId = referenceId,
                    status = LoyaltyWalletEventStatus.RESERVED,
                    amount = reward.amount,
                    createdAt = now,
                    updatedAt = now,
                )
            }

            transaction.set(
                walletRef,
                mapOf(
                    "nationalId" to cleanNationalId,
                    "updatedAt" to Timestamp(now),
                    "events" to events.map { it.toFirestoreMap() },
                ),
                SetOptions.merge(),
            )
            null
        }.awaitResult()
    }

    override suspend fun consumeRewards(nationalId: String, referenceId: String) {
        mutateReferenceStatus(nationalId, referenceId, LoyaltyWalletEventStatus.CONSUMED)
    }

    override suspend fun releaseRewards(nationalId: String, referenceId: String) {
        mutateReferenceStatus(nationalId, referenceId, LoyaltyWalletEventStatus.RELEASED)
    }

    private suspend fun mutateReferenceStatus(
        nationalId: String,
        referenceId: String,
        targetStatus: LoyaltyWalletEventStatus,
    ) {
        val cleanNationalId = nationalId.cleanNationalId()
        if (cleanNationalId.isEmpty() || referenceId.isBlank()) return

        val walletRef = firestore
            .collection(FirestoreCollections.CLIENT_LOYALTY_WALLETS)
            .document(cleanNationalId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(walletRef)
            val events = snapshot.walletEvents().map { event ->
                if (event.referenceId == referenceId && event.status == LoyaltyWalletEventStatus.RESERVED) {
                    event.copy(status = targetStatus, updatedAt = Date())
                } else {
                    event
                }
            }

            transaction.set(
                walletRef,
                mapOf(
                    "nationalId" to cleanNationalId,
                    "updatedAt" to Timestamp(Date()),
                    "events" to events.map { it.toFirestoreMap() },
                ),
                SetOptions.merge(),
            )
            null
        }.awaitResult()
    }

    private suspend fun fetchTemplates(): List<LoyaltyRewardTemplate> {
        val snapshot = firestore
            .collection(FirestoreCollections.LOYALTY_REWARD_TEMPLATES)
            .get()
            .awaitResult()

        return snapshot.documents
            .mapNotNull { it.toLoyaltyRewardTemplateOrNull() }
            .sortedWith(compareBy<LoyaltyRewardTemplate> { it.priority }.thenBy { it.title })
    }

    private suspend fun fetchWalletEvents(nationalId: String): List<LoyaltyWalletEvent> {
        val snapshot = firestore
            .collection(FirestoreCollections.CLIENT_LOYALTY_WALLETS)
            .document(nationalId)
            .get()
            .awaitResult()

        return snapshot.walletEvents()
    }

    private suspend fun computeTotals(nationalId: String): LoyaltyTotals {
        val ordersSnapshot = firestore
            .collection(FirestoreCollections.RESTAURANT_ORDERS)
            .whereEqualTo("nationalId", nationalId)
            .get()
            .awaitResult()

        val bookingsSnapshot = firestore
            .collection(FirestoreCollections.ADVENTURE_BOOKINGS)
            .whereEqualTo("nationalId", nationalId)
            .get()
            .awaitResult()

        val restaurantSpent = ordersSnapshot.documents
            .filter { it.getString("status")?.equals("completed", ignoreCase = true) == true }
            .sumOf { it.doubleValue("totalAmount") }

        val adventureSpent = bookingsSnapshot.documents
            .filter { it.getString("status")?.equals("completed", ignoreCase = true) == true }
            .sumOf { it.doubleValue("totalAmount") }

        return LoyaltyTotals(
            restaurantSpent = restaurantSpent,
            adventureSpent = adventureSpent,
        )
    }

    private fun adventureSubtotalFor(
        item: AdventureReservationItemDraft,
        activity: AdventureActivityCatalogItem,
    ): Double {
        val finalUnitPrice = (activity.basePrice - activity.discountAmount).coerceAtLeast(0.0)
        return when (item.activity) {
            AdventureActivityType.OFF_ROAD -> {
                val hours = item.durationMinutes.toDouble() / 60.0
                finalUnitPrice * hours * item.vehicleCount.toDouble()
            }

            AdventureActivityType.PAINTBALL,
            AdventureActivityType.GO_KARTS,
            AdventureActivityType.SHOOTING_RANGE,
                -> {
                val blocks = item.durationMinutes.toDouble() / 30.0
                finalUnitPrice * blocks * item.peopleCount.toDouble()
            }

            AdventureActivityType.CAMPING -> {
                finalUnitPrice * item.peopleCount.toDouble() * item.nights.toDouble()
            }

            AdventureActivityType.EXTREME_SLIDE -> {
                finalUnitPrice * item.peopleCount.toDouble()
            }
        }.roundMoney()
    }

    private data class LoyaltyTotals(
        val restaurantSpent: Double,
        val adventureSpent: Double,
    ) {
        val totalSpent: Double = restaurantSpent + adventureSpent
    }

    private fun DocumentSnapshot.toLoyaltyRewardTemplateOrNull(): LoyaltyRewardTemplate? {
        val rawRule = get("rule") as? Map<*, *> ?: return null
        val rule = LoyaltyRewardRule(
            type = parseRuleType(rawRule.stringValue("type")),
            percentage = rawRule.doubleValueOrNull("percentage"),
            menuItemId = rawRule.stringValueOrNull("menuItemId")
                ?: rawRule.stringValueOrNull("menu_item_id"),
            activityId = rawRule.stringValueOrNull("activityId")
                ?: rawRule.stringValueOrNull("activity_id"),
            quantity = rawRule.intValueOrNull("quantity"),
            buyQuantity = rawRule.intValueOrNull("buyQuantity")
                ?: rawRule.intValueOrNull("buy_quantity"),
            freeQuantity = rawRule.intValueOrNull("freeQuantity")
                ?: rawRule.intValueOrNull("free_quantity"),
            repeatable = rawRule.boolValueOrNull("repeatable"),
        )

        val resolvedTitle = stringValueOrNull("title")
            ?.takeIf { it.isNotBlank() }
            ?: defaultTitleFor(rule)

        return LoyaltyRewardTemplate(
            id = stringValueOrNull("id")?.takeIf { it.isNotBlank() } ?: id,
            title = resolvedTitle,
            subtitle = stringValueOrNull("subtitle").orEmpty(),
            scope = parseScope(stringValueOrNull("scope")),
            minimumLevel = parseLevel(
                stringValueOrNull("minimumLevel") ?: stringValueOrNull("minimum_level")
            ),
            triggerMode = parseTriggerMode(
                stringValueOrNull("triggerMode") ?: stringValueOrNull("trigger_mode")
            ),
            isActive = boolValue("isActive", default = boolValue("active", default = true)),
            canStack = boolValue("canStack", default = boolValue("can_stack", default = true)),
            priority = intValue("priority", default = 0),
            maxUsesPerClient = intValue(
                "maxUsesPerClient",
                default = intValue("max_uses_per_client", default = 1)
            ).coerceAtLeast(1),
            expiresInDays = intValueOrNull("expiresInDays") ?: intValueOrNull("expires_in_days"),
            rule = rule,
            createdAt = dateValue("createdAt") ?: dateValue("created_at") ?: Date(),
            updatedAt = dateValue("updatedAt") ?: dateValue("updated_at") ?: dateValue("createdAt")
            ?: Date(),
        )
    }

    private fun defaultTitleFor(rule: LoyaltyRewardRule): String {
        return when (rule.type) {
            LoyaltyRewardRuleType.SPECIFIC_MENU_ITEM_PERCENTAGE -> "Descuento en plato específico"
            LoyaltyRewardRuleType.MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE -> "Descuento en tu plato"
            LoyaltyRewardRuleType.FREE_MENU_ITEM -> "Plato gratis"
            LoyaltyRewardRuleType.BUY_X_GET_Y_FREE -> "Promoción especial"
            LoyaltyRewardRuleType.ACTIVITY_PERCENTAGE -> "Descuento en aventura"
        }
    }

    private fun DocumentSnapshot.walletEvents(): List<LoyaltyWalletEvent> {
        val rawEvents = get("events") as? List<*> ?: return emptyList()
        return rawEvents.mapNotNull { raw ->
            val map = raw as? Map<*, *> ?: return@mapNotNull null
            LoyaltyWalletEvent(
                id = map.stringValueOrNull("id") ?: return@mapNotNull null,
                templateId = map.stringValueOrNull("templateId")
                    ?: map.stringValueOrNull("template_id")
                    ?: return@mapNotNull null,
                templateTitle = map.stringValueOrNull("templateTitle")
                    ?: map.stringValueOrNull("template_title")
                    ?: "Premio Murco Loyalty",
                referenceType = parseReferenceType(
                    map.stringValueOrNull("referenceType")
                        ?: map.stringValueOrNull("reference_type")
                ),
                referenceId = map.stringValueOrNull("referenceId")
                    ?: map.stringValueOrNull("reference_id").orEmpty(),
                status = parseEventStatus(map.stringValueOrNull("status")),
                amount = map.doubleValue("amount"),
                createdAt = map.dateValue("createdAt") ?: map.dateValue("created_at") ?: Date(),
                updatedAt = map.dateValue("updatedAt") ?: map.dateValue("updated_at") ?: Date(),
            )
        }
    }

    private fun LoyaltyWalletEvent.toFirestoreMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "templateId" to templateId,
        "templateTitle" to templateTitle,
        "referenceType" to referenceType.name.lowercase(),
        "referenceId" to referenceId,
        "status" to status.name.lowercase(),
        "amount" to amount,
        "createdAt" to Timestamp(createdAt),
        "updatedAt" to Timestamp(updatedAt),
    )

    private fun usageCount(templateId: String, events: List<LoyaltyWalletEvent>): Int =
        events.count {
            it.templateId == templateId &&
                    (it.status == LoyaltyWalletEventStatus.RESERVED || it.status == LoyaltyWalletEventStatus.CONSUMED)
        }

    private fun parseScope(raw: String?): LoyaltyRewardScope = when (raw.normalizeKey()) {
        "restaurant" -> LoyaltyRewardScope.RESTAURANT
        "adventure" -> LoyaltyRewardScope.ADVENTURE
        "both" -> LoyaltyRewardScope.BOTH
        else -> LoyaltyRewardScope.BOTH
    }

    private fun parseTriggerMode(raw: String?): LoyaltyRewardTriggerMode =
        when (raw.normalizeKey()) {
            "manual" -> LoyaltyRewardTriggerMode.MANUAL
            else -> LoyaltyRewardTriggerMode.AUTOMATIC
        }

    private fun parseRuleType(raw: String?): LoyaltyRewardRuleType = when (raw.normalizeKey()) {
        "mostexpensivemenuitempercentage" -> LoyaltyRewardRuleType.MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE
        "specificmenuitempercentage" -> LoyaltyRewardRuleType.SPECIFIC_MENU_ITEM_PERCENTAGE
        "activitypercentage" -> LoyaltyRewardRuleType.ACTIVITY_PERCENTAGE
        "freemenuitem" -> LoyaltyRewardRuleType.FREE_MENU_ITEM
        "buyxgetyfree" -> LoyaltyRewardRuleType.BUY_X_GET_Y_FREE
        else -> LoyaltyRewardRuleType.MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE
    }

    private fun parseReferenceType(raw: String?): LoyaltyRewardReferenceType =
        when (raw.normalizeKey()) {
            "booking" -> LoyaltyRewardReferenceType.BOOKING
            else -> LoyaltyRewardReferenceType.ORDER
        }

    private fun parseEventStatus(raw: String?): LoyaltyWalletEventStatus =
        when (raw.normalizeKey()) {
            "consumed" -> LoyaltyWalletEventStatus.CONSUMED
            "released" -> LoyaltyWalletEventStatus.RELEASED
            "expired" -> LoyaltyWalletEventStatus.EXPIRED
            else -> LoyaltyWalletEventStatus.RESERVED
        }

    private fun parseLevel(raw: String?): LoyaltyLevel = when (raw.normalizeKey()) {
        "silver" -> LoyaltyLevel.SILVER
        "gold" -> LoyaltyLevel.GOLD
        "platinum" -> LoyaltyLevel.PLATINUM
        "diamond" -> LoyaltyLevel.DIAMOND
        else -> LoyaltyLevel.BRONZE
    }

    private fun String?.normalizeKey(): String = orEmpty()
        .replace("_", "")
        .replace("-", "")
        .trim()
        .lowercase()

    private fun String.cleanNationalId(): String = filter { it.isDigit() }

    private fun DocumentSnapshot.stringValueOrNull(field: String): String? =
        getString(field)?.trim()

    private fun DocumentSnapshot.boolValue(field: String, default: Boolean): Boolean =
        getBoolean(field) ?: default

    private fun DocumentSnapshot.intValue(field: String, default: Int): Int =
        intValueOrNull(field) ?: default

    private fun DocumentSnapshot.intValueOrNull(field: String): Int? =
        when (val value = get(field)) {
            is Int -> value
            is Long -> value.toInt()
            is Double -> value.toInt()
            is Number -> value.toInt()
            else -> null
        }

    private fun DocumentSnapshot.doubleValue(field: String): Double =
        when (val value = get(field)) {
            is Double -> value
            is Long -> value.toDouble()
            is Int -> value.toDouble()
            is Number -> value.toDouble()
            else -> 0.0
        }

    private fun DocumentSnapshot.dateValue(field: String): Date? = when (val value = get(field)) {
        is Timestamp -> value.toDate()
        is Date -> value
        else -> null
    }

    private fun Map<*, *>.stringValue(field: String): String = stringValueOrNull(field).orEmpty()

    private fun Map<*, *>.stringValueOrNull(field: String): String? =
        (this[field] as? String)?.trim()

    private fun Map<*, *>.doubleValue(field: String): Double = doubleValueOrNull(field) ?: 0.0

    private fun Map<*, *>.doubleValueOrNull(field: String): Double? =
        when (val value = this[field]) {
            is Double -> value
            is Long -> value.toDouble()
            is Int -> value.toDouble()
            is Number -> value.toDouble()
            else -> null
        }

    private fun Map<*, *>.intValueOrNull(field: String): Int? = when (val value = this[field]) {
        is Int -> value
        is Long -> value.toInt()
        is Double -> value.toInt()
        is Number -> value.toInt()
        else -> null
    }

    private fun Map<*, *>.boolValueOrNull(field: String): Boolean? = this[field] as? Boolean

    private fun Map<*, *>.dateValue(field: String): Date? = when (val value = this[field]) {
        is Timestamp -> value.toDate()
        is Date -> value
        else -> null
    }

    private fun Double.roundMoney(): Double = round(this * 100.0) / 100.0
}
