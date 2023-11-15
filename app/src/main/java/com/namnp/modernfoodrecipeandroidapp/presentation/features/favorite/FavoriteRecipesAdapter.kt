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
import com.namnp.modernfoodrecipeandroidapp.presentation.MainViewModel
import com.namnp.modernfoodrecipeandroidapp.presentation.features.recipe.RecipesDiffUtil
import kotlinx.android.synthetic.main.favorite_recipe_item.view.*

class FavoriteRecipesAdapter(
    private val requireActivity: FragmentActivity,
    private val mainViewModel: MainViewModel,
) : RecyclerView.Adapter<FavoriteRecipesAdapter.RecipeViewHolder>(), ActionMode.Callback {

    private var favoriteRecipes = emptyList<FavoritesEntity>()

    private var isMultiSelection = false
    private var selectedRecipes = arrayListOf<FavoritesEntity>()
    private var recipesViewHolders = arrayListOf<RecipeViewHolder>()
    private var actionMode: ActionMode? = null
    private lateinit var rootView: View

    class RecipeViewHolder(private val binding: FavoriteRecipeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(favoritesEntity: FavoritesEntity) {
            binding.favoritesEntity = favoritesEntity
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): RecipeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FavoriteRecipeItemBinding.inflate(layoutInflater, parent, false)
                return RecipeViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        return RecipeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        rootView = holder.itemView.rootView
        val currentRecipe = favoriteRecipes[position]
        holder.bind(currentRecipe)

        holder.itemView.recipesItemLayout.setOnClickListener {
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
        holder.itemView.recipesItemLayout.setOnLongClickListener {
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
        holder.itemView.recipesItemLayout.setBackgroundColor(
            ContextCompat.getColor(requireActivity, backgroundColor)
        )
        holder.itemView.favorite_row_cardView.strokeColor =
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
        actionMode?.let { actionMode ->
            actionMode.menuInflater?.inflate(R.menu.favorites_menu, menu)
            this.actionMode = actionMode
        }
        return true
    }

    private fun applyStatusBarColor(color: Int){
        requireActivity.window.statusBarColor = ContextCompat.getColor(requireActivity, color)
    }

    override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean = true

    override fun onActionItemClicked(actionMode: ActionMode?, menu: MenuItem?): Boolean {
        if(menu?.itemId == R.id.delete_favorite_recipe_menu){
            selectedRecipes.forEach {
                mainViewModel.deleteFavoriteRecipe(it)
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