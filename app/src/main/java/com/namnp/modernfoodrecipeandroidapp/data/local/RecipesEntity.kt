package com.namnp.modernfoodrecipeandroidapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.RECIPES_TABLE
import com.namnp.modernfoodrecipeandroidapp.data.models.FoodRecipe

@Entity(tableName = RECIPES_TABLE)
class RecipesEntity(
    var foodRecipe: FoodRecipe
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}