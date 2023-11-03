package com.namnp.modernfoodrecipeandroidapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.FAVORITE_RECIPES_TABLE
import com.namnp.modernfoodrecipeandroidapp.data.models.Result

@Entity(tableName = FAVORITE_RECIPES_TABLE)
class FavoritesEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var result: Result
)