package com.namnp.modernfoodrecipeandroidapp.data.models
import com.google.gson.annotations.SerializedName

data class FoodRecipe(
    @SerializedName("results")
    val results: List<Result>
)