package com.namnp.modernfoodrecipeandroidapp.presentation

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.namnp.modernfoodrecipeandroidapp.data.FoodRecipesRepository
import com.namnp.modernfoodrecipeandroidapp.data.local.RecipesEntity
import com.namnp.modernfoodrecipeandroidapp.data.models.FoodRecipe
import com.namnp.modernfoodrecipeandroidapp.util.NetworkResult
import com.namnp.modernfoodrecipeandroidapp.util.hasInternetConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception

class MainViewModel @ViewModelInject constructor(
    private val repository: FoodRecipesRepository,
    application: Application
) : AndroidViewModel(application) {

    /** LOCAL DATA */
    val localRecipes: LiveData<List<RecipesEntity>> = repository.localRecipes.readDatabase().asLiveData()

    private fun addRecipes(recipesEntity: RecipesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.localRecipes.insertRecipes(recipesEntity)
        }

    /** REMOTE DATA */
    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()

    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRemoteRecipes(queries)
    }

    private suspend fun getRemoteRecipes(queries: Map<String, String>) {
        recipesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remoteRecipes.getRecipes(queries)
                recipesResponse.value = handleFoodRecipesResponse(response)

                // SAVE TO LOCAL DB
                recipesResponse.value?.data?.let { foodRecipe ->
                    cacheRecipesToLocal(foodRecipe)
                }
            } catch (e: Exception) {
                recipesResponse.value = NetworkResult.Error("Recipes not found.")
            }
        } else {
            recipesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private fun handleFoodRecipesResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe> {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited.")
            }
            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error("Recipes not found.")
            }
            response.isSuccessful -> {
                val foodRecipes = response.body()
                return NetworkResult.Success(foodRecipes!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    private fun cacheRecipesToLocal(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe)
        addRecipes(recipesEntity)
    }

    private fun hasInternetConnection() = getApplication<Application>().hasInternetConnection()
}