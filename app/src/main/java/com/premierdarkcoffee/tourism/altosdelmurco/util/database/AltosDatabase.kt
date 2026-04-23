package com.premierdarkcoffee.tourism.altosdelmurco.util.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.CartDao
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.CartDraftEntity
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.CartItemEntity

@Database(
    entities = [
        CartDraftEntity::class,
        CartItemEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
@TypeConverters(RoomConverters::class)
abstract class AltosDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}
