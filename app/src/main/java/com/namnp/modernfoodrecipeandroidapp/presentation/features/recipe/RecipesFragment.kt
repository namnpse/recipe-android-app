package com.namnp.modernfoodrecipeandroidapp.presentation.features.recipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.namnp.modernfoodrecipeandroidapp.R
import com.namnp.modernfoodrecipeandroidapp.presentation.MainViewModel
import com.namnp.modernfoodrecipeandroidapp.util.NetworkResult
import kotlinx.android.synthetic.main.fragment_recipes.view.shimmerRecyclerView
import kotlinx.coroutines.launch

class RecipesFragment : Fragment(R.layout.fragment_recipes) {

    private lateinit var mainViewModel: MainViewModel
    private val recipesAdapter by lazy { RecipesAdapter() }
    private lateinit var view: View
    private lateinit var recipesViewModel: RecipesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        recipesViewModel = ViewModelProvider(requireActivity()).get(RecipesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_recipes, container, false)

        setupRecyclerView()
        getRecipes()

        return view
    }

    private fun getRemoteRecipes() {
        mainViewModel.getRecipes(recipesViewModel.getQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { recipesAdapter.setData(it) }
                }

                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadLocalRecipesData()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        }
    }

    // Follow Single Source Of Truth (Check Local -> if empty -> get Remote -> save to local -> display data)
    private fun getRecipes() {
        lifecycleScope.launch {
            // GET LOCAL FIRST
            mainViewModel.localRecipes.observe(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    recipesAdapter.setData(database[0].foodRecipe)
                    hideShimmerEffect()
                } else {
                    // GET REMOTE
                    getRemoteRecipes()
                }
            }
        }
    }

    private fun loadLocalRecipesData() {
        lifecycleScope.launch {
            mainViewModel.localRecipes.observe(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    recipesAdapter.setData(database[0].foodRecipe)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        view.shimmerRecyclerView.adapter = recipesAdapter
        view.shimmerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun showShimmerEffect() {
        view.shimmerRecyclerView.showShimmer()
    }

    private fun hideShimmerEffect() {
        view.shimmerRecyclerView.hideShimmer()
    }

}