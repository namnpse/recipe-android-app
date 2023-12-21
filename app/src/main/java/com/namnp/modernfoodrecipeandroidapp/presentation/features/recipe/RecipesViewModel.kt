package com.namnp.modernfoodrecipeandroidapp.presentation.features.recipe

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.API_KEY
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.DEFAULT_DIET_TYPE
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.DEFAULT_MEAL_TYPE
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.DEFAULT_RECIPES_NUMBER
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.QUERY_API_KEY
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.QUERY_DIET
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.QUERY_NUMBER
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.QUERY_SEARCH
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.QUERY_TYPE
import com.namnp.modernfoodrecipeandroidapp.data.DataStoreRepository
import com.namnp.modernfoodrecipeandroidapp.data.MealAndDietType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository
) : AndroidViewModel(application) {

    private var tempMealAndDiet: MealAndDietType? = null
    val readMealAndDietType = dataStoreRepository.readMealAndDietType
    var isBackOnline = false
    var networkStatus = false
    val getBackOnlineStatus = dataStoreRepository.readBackOnline.asLiveData()

    fun saveMealAndDietType() =
        viewModelScope.launch(Dispatchers.IO) {
            tempMealAndDiet?.let {
                dataStoreRepository.saveMealAndDietType(
                    it.selectedMealType,
                    it.selectedMealTypeId,
                    it.selectedDietType,
                    it.selectedDietTypeId
                )
            }
        }

    fun saveTemporaryMealAndDietType(mealType: String, mealTypeId: Int, dietType: String, dietTypeId: Int) {
        tempMealAndDiet = MealAndDietType(
            mealType,
            mealTypeId,
            dietType,
            dietTypeId
        )
    }

    fun getQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"
        tempMealAndDiet?.let {
            queries[QUERY_TYPE] = it.selectedMealType
            queries[QUERY_DIET] = it.selectedDietType
        }

        return queries
    }

    fun showNetworkStatus() {
        if (!networkStatus) {
            Toast.makeText(getApplication(), "No Internet Connection.", Toast.LENGTH_SHORT).show()
            saveBackOnline(true)
        } else {
            if (isBackOnline) {
                Toast.makeText(getApplication(), "Back online.", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }
    }

    private fun saveBackOnline(backOnline: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }

    fun applySearchRecipeQuery(searchQuery: String): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        queries[QUERY_SEARCH] = searchQuery
        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"
        return queries
    }

}