package com.namnp.modernfoodrecipeandroidapp.presentation.features.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.namnp.modernfoodrecipeandroidapp.data.models.FoodRecipe
import com.namnp.modernfoodrecipeandroidapp.data.models.Result
import com.namnp.modernfoodrecipeandroidapp.databinding.RecipeItemLayoutBinding

class RecipesAdapter : RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder>() {

    private var recipes = emptyList<Result>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RecipeItemLayoutBinding.inflate(layoutInflater, parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val currentRecipe = recipes[position]
        holder.bind(currentRecipe)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    fun setData(newData: FoodRecipe){
        val recipesDiffUtil = RecipesDiffUtil(recipes, newData.results)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        recipes = newData.results
        diffUtilResult.dispatchUpdatesTo(this)
    }

    class RecipeViewHolder(private val binding: RecipeItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(result: Result){
            binding.result = result
            binding.executePendingBindings()
        }
    }
}