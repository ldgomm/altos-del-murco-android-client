package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain

import com.google.firebase.Timestamp
import com.premierdarkcoffee.tourism.altosdelmurco.util.extrension.abbreviatedDateText
import java.util.Calendar
import java.util.Date

enum class LoyaltyRewardScope(val title: String) {
    RESTAURANT("Restaurante"), ADVENTURE("Aventura"), BOTH("Ambos");

    fun matchesRestaurant(): Boolean = this == RESTAURANT || this == BOTH

    fun matchesAdventure(): Boolean = this == ADVENTURE || this == BOTH
}

enum class LoyaltyRewardTriggerMode {
    AUTOMATIC, MANUAL,
}

enum class LoyaltyRewardRuleType {
    MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE, SPECIFIC_MENU_ITEM_PERCENTAGE, ACTIVITY_PERCENTAGE, FREE_MENU_ITEM, BUY_X_GET_Y_FREE,
}

data class LoyaltyRewardRule(
    val type: LoyaltyRewardRuleType,
    val percentage: Double? = null,
    val menuItemId: String? = null,
    val activityId: String? = null,
    val quantity: Int? = null,
    val buyQuantity: Int? = null,
    val freeQuantity: Int? = null,
    val repeatable: Boolean? = null,
) {
    companion object {
        fun mostExpensiveMenuItemDiscount(percentage: Double): LoyaltyRewardRule =
            LoyaltyRewardRule(
                type = LoyaltyRewardRuleType.MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE,
                percentage = percentage,
                quantity = 1,
            )

        fun specificMenuItemDiscount(
            menuItemId: String,
            percentage: Double,
            quantity: Int = 1,
        ): LoyaltyRewardRule = LoyaltyRewardRule(
            type = LoyaltyRewardRuleType.SPECIFIC_MENU_ITEM_PERCENTAGE,
            percentage = percentage,
            menuItemId = menuItemId,
            quantity = quantity.coerceAtLeast(1),
        )

        fun activityDiscount(
            activityId: String,
            percentage: Double,
        ): LoyaltyRewardRule = LoyaltyRewardRule(
            type = LoyaltyRewardRuleType.ACTIVITY_PERCENTAGE,
            percentage = percentage,
            activityId = activityId,
            quantity = 1,
        )

        fun freeMenuItem(
            menuItemId: String,
            quantity: Int = 1,
        ): LoyaltyRewardRule = LoyaltyRewardRule(
            type = LoyaltyRewardRuleType.FREE_MENU_ITEM,
            menuItemId = menuItemId,
            quantity = quantity.coerceAtLeast(1),
        )

        fun buyXGetYFree(
            menuItemId: String,
            buyQuantity: Int,
            freeQuantity: Int = 1,
            repeatable: Boolean = true,
        ): LoyaltyRewardRule = LoyaltyRewardRule(
            type = LoyaltyRewardRuleType.BUY_X_GET_Y_FREE,
            menuItemId = menuItemId,
            buyQuantity = buyQuantity.coerceAtLeast(1),
            freeQuantity = freeQuantity.coerceAtLeast(1),
            repeatable = repeatable,
        )
    }
}

data class LoyaltyRewardTemplate(
    val id: String,
    val title: String,
    val subtitle: String,
    val scope: LoyaltyRewardScope,
    val minimumLevel: LoyaltyLevel,
    val triggerMode: LoyaltyRewardTriggerMode,
    val isActive: Boolean,
    val canStack: Boolean,
    val priority: Int,
    val maxUsesPerClient: Int,
    val expiresInDays: Int?,
    val rule: LoyaltyRewardRule,
    val createdAt: Date,
    val updatedAt: Date,
) {
    val displaySummary: String
        get() = when (rule.type) {
            LoyaltyRewardRuleType.MOST_EXPENSIVE_MENU_ITEM_PERCENTAGE -> "${(rule.percentage ?: 0.0).toInt()}% en el plato elegible más caro"

            LoyaltyRewardRuleType.SPECIFIC_MENU_ITEM_PERCENTAGE -> "${(rule.percentage ?: 0.0).toInt()}% en item específico"

            LoyaltyRewardRuleType.ACTIVITY_PERCENTAGE -> "${(rule.percentage ?: 0.0).toInt()}% en actividad específica"

            LoyaltyRewardRuleType.FREE_MENU_ITEM -> "${(rule.quantity ?: 1).coerceAtLeast(1)} item(s) gratis"

            LoyaltyRewardRuleType.BUY_X_GET_Y_FREE -> "Compra ${
                (rule.buyQuantity ?: 1).coerceAtLeast(
                    1
                )
            } y recibe ${
                (rule.freeQuantity ?: 1).coerceAtLeast(
                    1
                )
            } gratis"
        }

    fun isEligible(level: LoyaltyLevel): Boolean = level.minimumSpent >= minimumLevel.minimumSpent

    val expirationDate: Date?
        get() {
            val days = expiresInDays ?: return null
            if (days <= 0) return null
            return Calendar.getInstance().apply {
                time = updatedAt
                add(Calendar.DAY_OF_YEAR, days)
            }.time
        }

    val isExpired: Boolean
        get() = expirationDate?.let { Date().after(it) } ?: false

    val expirationText: String?
        get() = expirationDate?.let { "Vence ${it.abbreviatedDateText()}" }

    val targetMenuItemId: String?
        get() = rule.menuItemId?.trim()?.takeIf { it.isNotEmpty() }

    val targetActivityId: String?
        get() = rule.activityId?.trim()?.takeIf { it.isNotEmpty() }
}

enum class LoyaltyRewardReferenceType {
    ORDER, BOOKING,
}

enum class LoyaltyWalletEventStatus {
    RESERVED, CONSUMED, RELEASED, EXPIRED,
}

data class LoyaltyWalletEvent(
    val id: String,
    val templateId: String,
    val templateTitle: String,
    val referenceType: LoyaltyRewardReferenceType,
    val referenceId: String,
    val status: LoyaltyWalletEventStatus,
    val amount: Double,
    val createdAt: Date,
    val updatedAt: Date,
)

data class AppliedReward(
    val id: String,
    val templateId: String,
    val title: String,
    val amount: Double,
    val note: String,
    val affectedMenuItemIds: List<String>,
    val affectedActivityIds: List<String>,
)

data class RewardWalletSnapshot(
    val userId: String,
    val currentLevel: LoyaltyLevel,
    val totalSpent: Double,
    val points: Int,
    val availableTemplates: List<LoyaltyRewardTemplate>,
    val reservedEvents: List<LoyaltyWalletEvent>,
    val consumedEvents: List<LoyaltyWalletEvent>,
    val releasedEvents: List<LoyaltyWalletEvent>,
) {
    companion object {
        fun empty(userId: String): RewardWalletSnapshot = RewardWalletSnapshot(
            userId = userId,
            currentLevel = LoyaltyLevel.BRONZE,
            totalSpent = 0.0,
            points = 0,
            availableTemplates = emptyList(),
            reservedEvents = emptyList(),
            consumedEvents = emptyList(),
            releasedEvents = emptyList(),
        )
    }
}

data class RewardComputationResult(
    val appliedRewards: List<AppliedReward>,
    val totalDiscount: Double,
    val walletSnapshot: RewardWalletSnapshot,
) {
    companion object {
        fun empty(wallet: RewardWalletSnapshot): RewardComputationResult = RewardComputationResult(
            appliedRewards = emptyList(),
            totalDiscount = 0.0,
            walletSnapshot = wallet,
        )
    }
}

data class RewardMenuLine(
    val menuItemId: String,
    val name: String,
    val unitPrice: Double,
    val quantity: Int,
)

data class RewardActivityLine(
    val activityId: String,
    val title: String,
    val linePrice: Double,
)

data class LoyaltyRewardTemplateDto(
    val id: String,
    val title: String,
    val subtitle: String,
    val scope: String,
    val minimumLevel: String,
    val triggerMode: String,
    val isActive: Boolean,
    val canStack: Boolean,
    val priority: Int,
    val maxUsesPerClient: Int,
    val expiresInDays: Int?,
    val rule: LoyaltyRewardRule,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
) {
    constructor(domain: LoyaltyRewardTemplate) : this(
        id = domain.id,
        title = domain.title,
        subtitle = domain.subtitle,
        scope = domain.scope.name.lowercase(),
        minimumLevel = domain.minimumLevel.name.lowercase(),
        triggerMode = domain.triggerMode.name.lowercase(),
        isActive = domain.isActive,
        canStack = domain.canStack,
        priority = domain.priority,
        maxUsesPerClient = domain.maxUsesPerClient.coerceAtLeast(1),
        expiresInDays = domain.expiresInDays,
        rule = domain.rule,
        createdAt = Timestamp(domain.createdAt),
        updatedAt = Timestamp(domain.updatedAt),
    )

    fun toDomain(): LoyaltyRewardTemplate = LoyaltyRewardTemplate(
        id = id,
        title = title,
        subtitle = subtitle,
        scope = LoyaltyRewardScope.entries.firstOrNull { it.name.equals(scope, ignoreCase = true) }
            ?: LoyaltyRewardScope.BOTH,
        minimumLevel = LoyaltyLevel.entries.firstOrNull {
            it.name.equals(
                minimumLevel, ignoreCase = true
            )
        } ?: LoyaltyLevel.BRONZE,
        triggerMode = LoyaltyRewardTriggerMode.entries.firstOrNull {
            it.name.equals(
                triggerMode, ignoreCase = true
            )
        } ?: LoyaltyRewardTriggerMode.AUTOMATIC,
        isActive = isActive,
        canStack = canStack,
        priority = priority,
        maxUsesPerClient = maxUsesPerClient.coerceAtLeast(1),
        expiresInDays = expiresInDays,
        rule = rule,
        createdAt = createdAt.toDate(),
        updatedAt = updatedAt.toDate(),
    )
}

data class LoyaltyWalletEventDto(
    val id: String,
    val templateId: String,
    val templateTitle: String,
    val referenceType: String,
    val referenceId: String,
    val status: String,
    val amount: Double,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
) {
    constructor(domain: LoyaltyWalletEvent) : this(
        id = domain.id,
        templateId = domain.templateId,
        templateTitle = domain.templateTitle,
        referenceType = domain.referenceType.name.lowercase(),
        referenceId = domain.referenceId,
        status = domain.status.name.lowercase(),
        amount = domain.amount,
        createdAt = Timestamp(domain.createdAt),
        updatedAt = Timestamp(domain.updatedAt),
    )

    fun toDomain(): LoyaltyWalletEvent = LoyaltyWalletEvent(
        id = id,
        templateId = templateId,
        templateTitle = templateTitle,
        referenceType = LoyaltyRewardReferenceType.entries.firstOrNull {
            it.name.equals(referenceType, ignoreCase = true)
        } ?: LoyaltyRewardReferenceType.ORDER,
        referenceId = referenceId,
        status = LoyaltyWalletEventStatus.entries.firstOrNull {
            it.name.equals(status, ignoreCase = true)
        } ?: LoyaltyWalletEventStatus.RESERVED,
        amount = amount,
        createdAt = createdAt.toDate(),
        updatedAt = updatedAt.toDate(),
    )
}

data class LoyaltyWalletDocument(
    val userId: String,
    val updatedAt: Date,
    val events: List<LoyaltyWalletEvent>,
)
