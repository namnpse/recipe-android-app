package com.namnp.modernfoodrecipeandroidapp.presentation.features.recipe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.namnp.modernfoodrecipeandroidapp.R
import com.namnp.modernfoodrecipeandroidapp.databinding.FragmentRecipesBinding
import com.namnp.modernfoodrecipeandroidapp.presentation.MainViewModel
import com.namnp.modernfoodrecipeandroidapp.presentation.features.recipe.bottomsheet.RecipesBottomSheetDirections
import com.namnp.modernfoodrecipeandroidapp.util.NetworkListener
import com.namnp.modernfoodrecipeandroidapp.util.NetworkResult
import com.namnp.modernfoodrecipeandroidapp.util.observeOnce
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class RecipesFragment : Fragment(R.layout.fragment_recipes), SearchView.OnQueryTextListener {

    private lateinit var mainViewModel: MainViewModel
    private val recipesAdapter by lazy { RecipesAdapter() }
    private lateinit var recipesViewModel: RecipesViewModel

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private val navArgs by navArgs<RecipesFragmentArgs>()

    private lateinit var networkListener: NetworkListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel

        setupRecyclerView()
        setHasOptionsMenu(true)
        getRecipes()

        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect { status ->
                    Log.d("NetworkListener", status.toString())
                    recipesViewModel.networkStatus = status
                    recipesViewModel.showNetworkStatus()
                    loadLocalRecipesData()
                }
        }

        binding.recipesFab.setOnClickListener {

            if (recipesViewModel.networkStatus) {
                // Approach 1
                findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
                // Approach 2
//                val action =
//                    RecipesFragmentDirections.actionRecipesFragmentToRecipesBottomSheet()
//                findNavController().navigate(action)
            } else {
                recipesViewModel.showNetworkStatus()
            }
        }

        recipesViewModel.getBackOnlineStatus.observe(viewLifecycleOwner) {
            recipesViewModel.isBackOnline = it
        }

        return binding.root
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
            mainViewModel.localRecipes.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty() && !navArgs.backFromBottomSheet) {
                    recipesAdapter.setData(database[0].foodRecipe)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.shimmerRecyclerView.adapter = recipesAdapter
        binding.shimmerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun showShimmerEffect() {
        binding.shimmerRecyclerView.showShimmer()
    }

    private fun hideShimmerEffect() {
        binding.shimmerRecyclerView.hideShimmer()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipes_menu, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}