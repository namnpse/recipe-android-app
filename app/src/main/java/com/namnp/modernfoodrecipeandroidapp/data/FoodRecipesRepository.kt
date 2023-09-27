package com.namnp.modernfoodrecipeandroidapp.data

import com.namnp.modernfoodrecipeandroidapp.data.local.RecipesLocalDataSource
import com.namnp.modernfoodrecipeandroidapp.data.remote.RecipesRemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class FoodRecipesRepository @Inject constructor(
    remoteDataSource: RecipesRemoteDataSource,
    localDataSource: RecipesLocalDataSource
) {

    val remoteRecipes = remoteDataSource
    val localRecipes = localDataSource

}