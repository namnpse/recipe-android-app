package com.namnp.modernfoodrecipeandroidapp.presentation.features.recipe

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.API_KEY
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.QUERY_API_KEY
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.QUERY_DIET
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.QUERY_NUMBER
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.QUERY_TYPE

class RecipesViewModel(application: Application) : AndroidViewModel(application) {

    fun getQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        queries[QUERY_NUMBER] = "20"
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_TYPE] = "snack"
        queries[QUERY_DIET] = "vegan"
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        return queries
    }

}