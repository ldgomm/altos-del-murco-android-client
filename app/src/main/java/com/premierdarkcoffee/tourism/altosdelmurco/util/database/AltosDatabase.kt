package com.premierdarkcoffee.tourism.altosdelmurco.util.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.local.CartDao
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.local.CartDraftEntity
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.local.CartItemEntity

@Database(
    entities = [
        CartDraftEntity::class,
        CartItemEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
@TypeConverters(RoomConverters::class)
abstract class AltosDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}
