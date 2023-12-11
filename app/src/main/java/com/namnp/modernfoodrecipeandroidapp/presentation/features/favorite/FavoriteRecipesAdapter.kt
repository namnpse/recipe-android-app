package com.namnp.modernfoodrecipeandroidapp.presentation.features.favorite

import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.namnp.modernfoodrecipeandroidapp.R
import com.namnp.modernfoodrecipeandroidapp.data.local.FavoritesEntity
import com.namnp.modernfoodrecipeandroidapp.databinding.FavoriteRecipeItemBinding
import com.namnp.modernfoodrecipeandroidapp.presentation.features.recipe.RecipesDiffUtil

class FavoriteRecipesAdapter(
    private val requireActivity: FragmentActivity,
    private val onDeleteFavoriteRecipe: ((ArrayList<FavoritesEntity>) -> Unit)?,
) : RecyclerView.Adapter<FavoriteRecipesAdapter.RecipeViewHolder>(), ActionMode.Callback {

    private var favoriteRecipes = emptyList<FavoritesEntity>()

    private var isMultiSelection = false
    private var selectedRecipes = arrayListOf<FavoritesEntity>()
    private var allViewHolders = arrayListOf<RecipeViewHolder>()
    private var recipesViewHolders = arrayListOf<RecipeViewHolder>()
    private var actionMode: ActionMode? = null
    private lateinit var rootView: View

    class RecipeViewHolder(val binding: FavoriteRecipeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(favoritesEntity: FavoritesEntity) {
            binding.favoritesEntity = favoritesEntity
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): RecipeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FavoriteRecipeItemBinding.inflate(layoutInflater)
                return RecipeViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        return RecipeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        rootView = holder.itemView.rootView
        allViewHolders.add(holder)
        val currentRecipe = favoriteRecipes[position]
        holder.bind(currentRecipe)

        saveItemStateOnScroll(currentRecipe, holder)

        holder.binding.recipesItemLayout.setOnClickListener {
            if (isMultiSelection) {
                applySelection(holder, currentRecipe)
            } else {
                val action =
                    FavoriteRecipesFragmentDirections.actionFavoriteRecipesFragmentToDetailsActivity(
                        currentRecipe.result
                    )
                holder.itemView.findNavController().navigate(action)
            }
        }

        /**
         * Handle long click
         * */
        holder.binding.recipesItemLayout.setOnLongClickListener {
            if (!isMultiSelection) {
                isMultiSelection = true
                requireActivity.startActionMode(this)
                applySelection(holder, currentRecipe)
                true
            } else {
                isMultiSelection = false
                false
            }
        }

        recipesViewHolders.add(holder)
    }

    private fun saveItemStateOnScroll(currentRecipe: FavoritesEntity, holder: RecipeViewHolder){
        if (selectedRecipes.contains(currentRecipe)) {
            changeRecipeStyle(holder, R.color.cardBackgroundLightColor, R.color.colorPrimary)
        } else {
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
        }
    }

    private fun applySelection(holder: RecipeViewHolder, currentRecipe: FavoritesEntity) {
        if (selectedRecipes.contains(currentRecipe)) {
            selectedRecipes.remove(currentRecipe)
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
        } else {
            selectedRecipes.add(currentRecipe)
            changeRecipeStyle(holder, R.color.cardBackgroundLightColor, R.color.colorPrimary)
        }
        applyActionModeTitle()
    }

    private fun changeRecipeStyle(holder: RecipeViewHolder, backgroundColor: Int, strokeColor: Int) {
        holder.binding.recipesItemLayout.setBackgroundColor(
            ContextCompat.getColor(requireActivity, backgroundColor)
        )
        holder.binding.favoriteRowCardView.strokeColor =
            ContextCompat.getColor(requireActivity, strokeColor)
    }

    private fun applyActionModeTitle(){
        actionMode?.let { actionMode ->
            when(selectedRecipes.size) {
                0 -> {
                    actionMode.finish()
                }
                1 -> {
                    actionMode.title = "${selectedRecipes.size} item selected"
                }
                else -> {
                    actionMode.title = "${selectedRecipes.size} items selected"
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return favoriteRecipes.size
    }

    fun updateData(newFavoriteRecipes: List<FavoritesEntity>){
        val favoriteRecipesDiffUtil =
            RecipesDiffUtil(favoriteRecipes, newFavoriteRecipes)
        val diffUtil = DiffUtil.calculateDiff(favoriteRecipesDiffUtil)
        favoriteRecipes = newFavoriteRecipes
        diffUtil.dispatchUpdatesTo(this)
    }

    override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        applyStatusBarColor(R.color.contextualStatusBarColor)
        actionMode?.let { am ->
            am.menuInflater?.inflate(R.menu.favorites_menu, menu)
            this.actionMode = am
        }
        return true
    }

    private fun applyStatusBarColor(color: Int){
        requireActivity.window.statusBarColor = ContextCompat.getColor(requireActivity, color)
    }

    override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean = true

    override fun onActionItemClicked(actionMode: ActionMode?, menu: MenuItem?): Boolean {
        if(menu?.itemId == R.id.delete_favorite_recipe_menu){
            if(selectedRecipes.isNotEmpty()) {
                onDeleteFavoriteRecipe?.invoke(selectedRecipes)
            }
            showSnackBar("${selectedRecipes.size} Recipe(s) removed.")
            isMultiSelection = false
            selectedRecipes.clear()
            actionMode?.finish()
        }
        return true
    }

    private fun showSnackBar(message: String){
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
            .setAction("OK"){}
            .show()
    }

    fun clearContextualActionMode() {
        actionMode?.finish()
    }

    override fun onDestroyActionMode(p0: ActionMode?) {
        recipesViewHolders.forEach { holder ->
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
        }
        isMultiSelection = false
        selectedRecipes.clear()
    }
}