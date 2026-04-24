package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuCategory
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuItem
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuSection
import com.premierdarkcoffee.tourism.altosdelmurco.util.constant.FirestoreCollections
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.component1
import kotlin.collections.component2

@Singleton
class MenuRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : MenuRepositoriable {

    override fun observeMenu(): Flow<List<MenuSection>> = callbackFlow {
        val registration: ListenerRegistration = firestore
            .collection(FirestoreCollections.RESTAURANT_MENU_ITEMS)
            .addSnapshotListener { snapshot, error ->
                when {
                    error != null -> close(error)
                    snapshot == null -> trySend(emptyList()).isSuccess
                    else -> {
                        val items = snapshot.documents.mapNotNull { document ->
                            MenuItemDto.fromDocument(document)
                                ?.toDomain(documentId = document.id)
                                ?.takeIf { it.id.isNotBlank() && it.name.isNotBlank() }
                        }
                        trySend(groupIntoSections(items)).isSuccess
                    }
                }
            }

        awaitClose { registration.remove() }
    }

    private fun groupIntoSections(items: List<MenuItem>): List<MenuSection> {
        return items
            .distinctBy { it.id }
            .groupBy { it.categoryId.ifBlank { "otros" } }
            .mapNotNull { (categoryId, categoryItems) ->
                val first = categoryItems.firstOrNull() ?: return@mapNotNull null
                MenuSection(
                    id = categoryId,
                    category = MenuCategory(
                        id = categoryId,
                        title = first.categoryTitle.ifBlank { "Otros" },
                    ),
                    items = categoryItems.sortedWith(compareBy<MenuItem> { it.sortOrder }.thenBy { it.name }),
                )
            }
            .sortedWith(
                compareBy<MenuSection> { categoryRank(it.category.title) }
                    .thenBy { it.category.title },
            )
    }

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
