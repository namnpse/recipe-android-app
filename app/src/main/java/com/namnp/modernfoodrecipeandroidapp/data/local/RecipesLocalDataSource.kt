package com.namnp.modernfoodrecipeandroidapp.data.local

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecipesLocalDataSource @Inject constructor(
    private val recipesDao: RecipesDao
) {
    fun getRecipes(): Flow<List<RecipesEntity>> {
        return recipesDao.getRecipes()
    }

    suspend fun addRecipes(recipesEntity: RecipesEntity) {
        recipesDao.addRecipes(recipesEntity)
    }

    fun getFavoriteRecipes(): Flow<List<FavoritesEntity>> {
        return recipesDao.getFavoriteRecipes()
    }

    suspend fun insertFavoriteRecipes(favoritesEntity: FavoritesEntity) {
        recipesDao.addFavoriteRecipe(favoritesEntity)
    }

    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) {
        recipesDao.deleteFavoriteRecipe(favoritesEntity)
    }

    suspend fun deleteAllFavoriteRecipes() {
        recipesDao.deleteAllFavoriteRecipes()
    }

    fun getFoodJoke(): Flow<List<FoodJokeEntity>> {
        return recipesDao.getFoodJoke()
    }

    suspend fun addFoodJoke(foodJokeEntity: FoodJokeEntity) {
        recipesDao.addFoodJoke(foodJokeEntity)
    }

}