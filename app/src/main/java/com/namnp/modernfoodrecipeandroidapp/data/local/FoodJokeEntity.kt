package com.namnp.modernfoodrecipeandroidapp.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.FOOD_JOKE_TABLE
import com.namnp.modernfoodrecipeandroidapp.data.models.FoodJoke

@Entity(tableName = FOOD_JOKE_TABLE)
class FoodJokeEntity(
    @Embedded
    var foodJoke: FoodJoke
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}