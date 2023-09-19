package com.namnp.modernfoodrecipeandroidapp.data.remote

import com.namnp.modernfoodrecipeandroidapp.data.models.FoodRecipe
import retrofit2.Response
import javax.inject.Inject

class RecipesRemoteDataSource @Inject constructor(
    private val foodRecipesApi: FoodRecipesApi
)  {
    suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipe> {
        return foodRecipesApi.getRecipes(queries)
    }
}