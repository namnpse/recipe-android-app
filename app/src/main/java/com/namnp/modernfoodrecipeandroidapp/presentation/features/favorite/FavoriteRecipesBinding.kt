package com.namnp.modernfoodrecipeandroidapp.presentation.features.favorite

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.namnp.modernfoodrecipeandroidapp.data.local.FavoritesEntity

class FavoriteRecipesBinding {

    companion object {

        @BindingAdapter("data", "setAdapter", requireAll = false)
        @JvmStatic
        fun setDataAndViewVisibility(
            view: View,
            favoritesEntity: List<FavoritesEntity>?,
            adapter: FavoriteRecipesAdapter?
        ) {
            if (favoritesEntity.isNullOrEmpty()) {
                when (view) {
                    is ImageView -> {
                        view.visibility = View.VISIBLE
                    }

                    is TextView -> {
                        view.visibility = View.VISIBLE
                    }

                    is RecyclerView -> {
                        view.visibility = View.INVISIBLE
                    }
                }
            } else {
                when (view) {
                    is ImageView -> {
                        view.visibility = View.INVISIBLE
                    }

                    is TextView -> {
                        view.visibility = View.INVISIBLE
                    }

                    is RecyclerView -> {
                        view.visibility = View.VISIBLE
                        adapter?.updateData(favoritesEntity)
                    }
                }
            }
        }

        @BindingAdapter("data", "setAdapter", requireAll = false)
        @JvmStatic
        fun setVisibility(
            view: View,
            favoritesEntity: List<FavoritesEntity>?,
            adapter: FavoriteRecipesAdapter?,
        ) {
            when (view) {
                is RecyclerView -> {
                    val hasNoData = favoritesEntity.isNullOrEmpty()
                    view.isInvisible = hasNoData
                    if (!hasNoData) {
                        favoritesEntity?.let { adapter?.updateData(it) }
                    }
                }

                else -> view.isVisible = favoritesEntity.isNullOrEmpty()
            }
        }


    }

}