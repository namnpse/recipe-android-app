package com.namnp.modernfoodrecipeandroidapp.presentation.features.recipe

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.namnp.modernfoodrecipeandroidapp.data.local.RecipesEntity
import com.namnp.modernfoodrecipeandroidapp.data.models.FoodRecipe
import com.namnp.modernfoodrecipeandroidapp.util.NetworkResult

class RecipesFragmentBindingAdapter {

    companion object {

        @BindingAdapter("apiResponse", "localData", requireAll = true)
        @JvmStatic
        fun handleReadDataErrors(
            view: View,
            apiResponse: NetworkResult<FoodRecipe>?,
            localData: List<RecipesEntity>?
        ) {
            when (view) {
                is ImageView -> {
                    view.isVisible = apiResponse is NetworkResult.Error && localData.isNullOrEmpty()
                }

                is TextView -> {
                    view.isVisible = apiResponse is NetworkResult.Error && localData.isNullOrEmpty()
                    view.text = apiResponse?.message.toString()
                }
            }
        }

    }

}