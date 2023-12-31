package com.namnp.modernfoodrecipeandroidapp.presentation

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.namnp.modernfoodrecipeandroidapp.R
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.API_KEY
import com.namnp.modernfoodrecipeandroidapp.data.FoodRecipesRepository
import com.namnp.modernfoodrecipeandroidapp.data.local.FavoritesEntity
import com.namnp.modernfoodrecipeandroidapp.data.local.FoodJokeEntity
import com.namnp.modernfoodrecipeandroidapp.data.local.RecipesEntity
import com.namnp.modernfoodrecipeandroidapp.data.models.FoodJoke
import com.namnp.modernfoodrecipeandroidapp.data.models.FoodRecipe
import com.namnp.modernfoodrecipeandroidapp.util.NetworkResult
import com.namnp.modernfoodrecipeandroidapp.util.UiText
import com.namnp.modernfoodrecipeandroidapp.util.hasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: FoodRecipesRepository,
    application: Application
) : AndroidViewModel(application) {

    /** LOCAL DATA */
    val localRecipes: LiveData<List<RecipesEntity>> = repository.localRecipes.getRecipes().asLiveData()
    val localFavoriteRecipes: LiveData<List<FavoritesEntity>> = repository.localRecipes.getFavoriteRecipes().asLiveData()
    val localFoodJoke: LiveData<List<FoodJokeEntity>> = repository.localRecipes.getFoodJoke().asLiveData()
    var recyclerViewState: Parcelable? = null

    private fun addRecipes(recipesEntity: RecipesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.localRecipes.addRecipes(recipesEntity)
        }

    fun addFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.localRecipes.insertFavoriteRecipes(favoritesEntity)
        }

    fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.localRecipes.deleteFavoriteRecipe(favoritesEntity)
        }

    private fun addFoodJoke(foodJokeEntity: FoodJokeEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.localRecipes.addFoodJoke(foodJokeEntity)
        }

    fun deleteAllFavoriteRecipes() =
        viewModelScope.launch(Dispatchers.IO) {
            repository.localRecipes.deleteAllFavoriteRecipes()
        }

    /** REMOTE DATA */
    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var searchedRecipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var foodJokeResponse: MutableLiveData<NetworkResult<FoodJoke>> = MutableLiveData()

    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRemoteRecipes(queries)
    }

    fun searchRecipes(searchQuery: Map<String, String>) = viewModelScope.launch {
        searchRecipesSafeCall(searchQuery)
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
                recipesResponse.value = NetworkResult.Error(UiText.StringResource(R.string.error_recipes_not_found))
            }
        } else {
            recipesResponse.value = NetworkResult.Error(UiText.StringResource(R.string.error_no_internet_connection))
        }
    }

    private fun handleFoodRecipesResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe> {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error(UiText.StringResource(R.string.error_timeout))
            }
            response.code() == 402 -> {
                return NetworkResult.Error(UiText.StringResource(R.string.error_api_key_limited))
            }
            response.body()!!.results.isEmpty() -> {
                return NetworkResult.Error(UiText.StringResource(R.string.error_recipes_not_found))
            }
            response.isSuccessful -> {
                val foodRecipes = response.body()
                return NetworkResult.Success(foodRecipes!!)
            }
            else -> {
                return NetworkResult.Error(UiText.PureString(response.message()))
            }
        }
    }

    private fun cacheRecipesToLocal(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe)
        addRecipes(recipesEntity)
    }

    private suspend fun searchRecipesSafeCall(searchQuery: Map<String, String>) {
        searchedRecipesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remoteRecipes.searchRecipes(searchQuery)
                searchedRecipesResponse.value = handleFoodRecipesResponse(response)
            } catch (e: Exception) {
                searchedRecipesResponse.value = NetworkResult.Error(UiText.StringResource(R.string.error_recipes_not_found))
            }
        } else {
            searchedRecipesResponse.value = NetworkResult.Error(UiText.StringResource(R.string.error_no_internet_connection))
        }
    }

    fun getFoodJoke() = viewModelScope.launch {
        getRemoteFoodJoke(API_KEY)
    }

    private suspend fun getRemoteFoodJoke(apiKey: String) {
        foodJokeResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remoteRecipes.getFoodJoke(apiKey)
                foodJokeResponse.value = handleFoodJokeResponse(response)
                foodJokeResponse.value!!.data?.let { foodJoke ->
                    cacheOfflineFoodJoke(foodJoke)
                }
            } catch (e: Exception) {
                foodJokeResponse.value = NetworkResult.Error(UiText.StringResource(R.string.error_recipes_not_found))
            }
        } else {
            foodJokeResponse.value = NetworkResult.Error(UiText.StringResource(R.string.error_no_internet_connection))
        }
    }

    private fun handleFoodJokeResponse(response: Response<FoodJoke>): NetworkResult<FoodJoke>? {
        return when {
            response.isSuccessful -> {
                val foodJoke = response.body()
                NetworkResult.Success(foodJoke!!)
            }
            response.code() == 402 -> {
                NetworkResult.Error(UiText.StringResource(R.string.error_api_key_limited))
            }
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error(UiText.StringResource(R.string.error_timeout))
            }
            else -> {
                NetworkResult.Error(UiText.PureString(response.message()))
            }
        }
    }

    private fun cacheOfflineFoodJoke(foodJoke: FoodJoke) {
        val foodJokeEntity = FoodJokeEntity(foodJoke)
        addFoodJoke(foodJokeEntity)
    }

    private fun hasInternetConnection() = getApplication<Application>().hasInternetConnection()
}