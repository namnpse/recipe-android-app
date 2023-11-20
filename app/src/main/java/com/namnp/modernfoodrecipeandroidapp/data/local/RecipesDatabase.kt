package com.namnp.modernfoodrecipeandroidapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        RecipesEntity::class,
        FavoritesEntity::class,
        FoodJokeEntity::class,
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(RecipesTypeConverter::class)
abstract class RecipesDatabase: RoomDatabase() {
    abstract val recipesDao: RecipesDao
}