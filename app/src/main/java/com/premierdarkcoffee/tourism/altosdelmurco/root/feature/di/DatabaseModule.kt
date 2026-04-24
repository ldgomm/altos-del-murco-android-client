package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di

import android.content.Context
import androidx.room.Room
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.local.CartDao
import com.premierdarkcoffee.tourism.altosdelmurco.util.database.AltosDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAltosDatabase(
        @ApplicationContext context: Context,
    ): AltosDatabase = Room.databaseBuilder(
        context,
        AltosDatabase::class.java,
        "altos_database",
    ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideCartDao(database: AltosDatabase): CartDao = database.cartDao()
}
