package com.namnp.modernfoodrecipeandroidapp.presentation.features.favorite

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.namnp.modernfoodrecipeandroidapp.R
import com.namnp.modernfoodrecipeandroidapp.databinding.FragmentFavoriteRecipesBinding
import com.namnp.modernfoodrecipeandroidapp.presentation.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class FavoriteRecipesFragment : Fragment() {

    private val mainViewModel: MainViewModel by viewModels()
    private val favoriteRecipesAdapter: FavoriteRecipesAdapter by lazy {
        FavoriteRecipesAdapter(
            requireActivity(),
            onDeleteFavoriteRecipe = { selectedRecipes ->
                selectedRecipes.forEach {
                    mainViewModel.deleteFavoriteRecipe(it)
                }
            },
        )
    }

    private var _binding: FragmentFavoriteRecipesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFavoriteRecipesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel
        binding.favoriteRecipesAdapter = favoriteRecipesAdapter

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.favorite_recipes_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.deleteAll_favorite_recipes_menu) {
                    mainViewModel.deleteAllFavoriteRecipes()
                    showSnackBar()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        setupRecyclerView(binding.favoriteRecipesRecyclerView)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(binding.favoriteRecipesRecyclerView)
        setHasOptionsMenu(true)

        mainViewModel.localFavoriteRecipes.observe(viewLifecycleOwner) { favoritesEntity ->
            favoriteRecipesAdapter.updateData(favoritesEntity)
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = favoriteRecipesAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showSnackBar(){
        Snackbar.make(binding.root, "All recipes are removed", Snackbar.LENGTH_SHORT)
            .setAction("OK"){}
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        favoriteRecipesAdapter.clearContextualActionMode()
    }

}