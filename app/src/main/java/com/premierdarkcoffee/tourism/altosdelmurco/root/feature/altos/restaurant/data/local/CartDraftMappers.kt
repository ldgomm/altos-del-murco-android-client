package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.local

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.CartDraftEntity
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.CartDraftWithItems
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.CartItemEntity
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.CartItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderDraft
import java.util.Date

internal fun OrderDraft.toEntity(): CartDraftEntity = CartDraftEntity(
    id = id,
    nationalId = nationalId,
    clientName = clientName,
    tableNumber = tableNumber,
    revision = revision,
    lastConfirmedRevision = lastConfirmedRevision,
    createdAtMillis = createdAt.time,
    updatedAtMillis = updatedAt.time,
)

internal fun CartItem.toEntity(draftId: String): CartItemEntity = CartItemEntity(
    id = id,
    draftId = draftId,
    menuItemId = menuItem.id,
    categoryId = menuItem.categoryId,
    categoryTitle = menuItem.categoryTitle,
    name = menuItem.name,
    description = menuItem.description,
    notes = menuItem.notes,
    ingredients = menuItem.ingredients,
    quantity = quantity,
    unitPrice = unitPrice,
    offerPrice = menuItem.offerPrice,
    imageURL = menuItem.imageURL,
    isAvailable = menuItem.isAvailable,
    remainingQuantity = menuItem.remainingQuantity,
    isFeatured = menuItem.isFeatured,
    sortOrder = menuItem.sortOrder,
    itemNotes = notes,
)

internal fun CartDraftWithItems.toDomain(): OrderDraft = OrderDraft(
    id = draft.id,
    nationalId = draft.nationalId,
    clientName = draft.clientName,
    tableNumber = draft.tableNumber,
    createdAt = Date(draft.createdAtMillis),
    updatedAt = Date(draft.updatedAtMillis),
    items = items.map { item ->
        CartItem(
            id = item.id,
            menuItem = MenuItem(
                id = item.menuItemId,
                categoryId = item.categoryId,
                categoryTitle = item.categoryTitle,
                name = item.name,
                description = item.description,
                notes = item.notes,
                ingredients = item.ingredients,
                price = item.unitPrice,
                offerPrice = item.offerPrice,
                imageURL = item.imageURL,
                isAvailable = item.isAvailable,
                remainingQuantity = item.remainingQuantity,
                isFeatured = item.isFeatured,
                sortOrder = item.sortOrder,
            ),
            quantity = item.quantity,
            notes = item.itemNotes,
        )
    },
    revision = draft.revision,
    lastConfirmedRevision = draft.lastConfirmedRevision,
)
