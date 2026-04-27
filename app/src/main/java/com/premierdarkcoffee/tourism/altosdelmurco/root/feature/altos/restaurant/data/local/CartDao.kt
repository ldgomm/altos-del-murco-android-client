package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Transaction
    @Query("SELECT * FROM cart_drafts WHERE id = :draftId")
    fun observeCart(draftId: String = CartDraftEntity.DEFAULT_ID): Flow<CartDraftWithItems?>

    @Upsert
    suspend fun upsertDraft(draft: CartDraftEntity)

    @Upsert
    suspend fun upsertItems(items: List<CartItemEntity>)

    @Query("DELETE FROM cart_items WHERE draftId = :draftId")
    suspend fun deleteItemsForDraft(draftId: String = CartDraftEntity.DEFAULT_ID)

    @Query("DELETE FROM cart_drafts WHERE id = :draftId")
    suspend fun deleteDraft(draftId: String = CartDraftEntity.DEFAULT_ID)

    @Transaction
    suspend fun replaceDraft(
        draft: CartDraftEntity,
        items: List<CartItemEntity>,
    ) {
        deleteItemsForDraft(draft.id)
        upsertDraft(draft)
        if (items.isNotEmpty()) upsertItems(items)
    }

    @Transaction
    suspend fun clearAll(draftId: String = CartDraftEntity.DEFAULT_ID) {
        deleteItemsForDraft(draftId)
        deleteDraft(draftId)
    }

    @Query(
        """
    UPDATE cart_drafts
    SET nationalId = :nationalId,
        clientName = :clientName,
        updatedAtMillis = :updatedAtMillis
    WHERE id = :draftId
    """
    )
    suspend fun updateClientInfo(
        draftId: String = CartDraftEntity.DEFAULT_ID,
        nationalId: String?,
        clientName: String,
        updatedAtMillis: Long,
    ): Int
}
