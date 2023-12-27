package com.namnp.modernfoodrecipeandroidapp.presentation.features.recipe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.namnp.modernfoodrecipeandroidapp.R
import com.namnp.modernfoodrecipeandroidapp.databinding.FragmentRecipesBinding
import com.namnp.modernfoodrecipeandroidapp.presentation.MainViewModel
import com.namnp.modernfoodrecipeandroidapp.util.NetworkListener
import com.namnp.modernfoodrecipeandroidapp.util.NetworkResult
import com.namnp.modernfoodrecipeandroidapp.util.observeOnce
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.appcompat.widget.SearchView as SearchViewAndroidX

@ExperimentalCoroutinesApi
class RecipesFragment : Fragment(R.layout.fragment_recipes),
    SearchViewAndroidX.OnQueryTextListener {

//    private lateinit var mainViewModel: MainViewModel
    private val mainViewModel: MainViewModel by viewModels()
    private val recipesAdapter by lazy { RecipesAdapter() }
//    private lateinit var recipesViewModel: RecipesViewModel
    private val recipesViewModel: RecipesViewModel by viewModels()

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private val navArgs by navArgs<RecipesFragmentArgs>()

    private lateinit var networkListener: NetworkListener
    private var shouldRequestData = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
//        recipesViewModel = ViewModelProvider(this)[RecipesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.recipes_menu, menu)

                val search = menu.findItem(R.id.menu_search)
                val searchView = search.actionView as? androidx.appcompat.widget.SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@RecipesFragment)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        setupRecyclerView()

        recipesViewModel.getBackOnlineStatus.observe(viewLifecycleOwner) {
            recipesViewModel.isBackOnline = it
        }

        //        lifecycleScope.launchWhenStarted { launchWhenStarted is deprecated, use repeatOnLifecycle(Lifecycle.State.STARTED) instead
        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                recipesViewModel.sharedFlowEvent.collectLatest { message ->
                    Toast.makeText(requireContext(), message.asString(requireContext()), Toast.LENGTH_SHORT).show()
                }
                networkListener = NetworkListener()
                networkListener.checkNetworkAvailability(requireContext())
                    .collect { status ->
                        Log.d("NetworkListener", status.toString())
                        recipesViewModel.networkStatus = status
                        recipesViewModel.showNetworkStatus()
                        loadLocalRecipesData()
                    }
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

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.recyclerViewState?.let { state ->
            binding.recicpeRecyclerView.layoutManager?.onRestoreInstanceState(state)
        }
    }

    private fun getRemoteRecipes() {
        mainViewModel.getRecipes(recipesViewModel.getQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { recipesAdapter.setData(it) }
                    recipesViewModel.saveMealAndDietType()
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
//            repeatOnLifecycle(Lifecycle.State.STARTED) { // don't need, live data already have own its lifecycle awareness, use it for flow
        // GET LOCAL FIRST
        mainViewModel.localRecipes.observe(viewLifecycleOwner) { database ->
            if (database.isNotEmpty()) {
                recipesAdapter.setData(database.first().foodRecipe)
            } else {
                // GET REMOTE
                getRemoteRecipes()
            }
        }
//            }
    }

    private fun loadLocalRecipesData() {
        mainViewModel.localRecipes.observeOnce(viewLifecycleOwner) { database ->
            if (database.isNotEmpty()
                && (!navArgs.backFromBottomSheet || shouldRequestData)
            ) {
                recipesAdapter.setData(database.first().foodRecipe)
                hideShimmerEffect()
            } else {
                if (!shouldRequestData) {
                    getRemoteRecipes()
                    shouldRequestData = true
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recicpeRecyclerView.apply {
            adapter = recipesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        showShimmerEffect()
    }

    private fun showShimmerEffect() {
        binding.recicpeRecyclerView.visibility = View.VISIBLE
        binding.recicpeRecyclerView.visibility = View.GONE
        binding.shimmerFrameLayout.startShimmer()
    }

    private fun hideShimmerEffect() {
        binding.shimmerFrameLayout.stopShimmer()
        binding.shimmerFrameLayout.visibility = View.GONE
        binding.recicpeRecyclerView.visibility = View.VISIBLE
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrEmpty()) {
            searchRecipeData(query)
        }
        return true
    }

    private fun searchRecipeData(searchQuery: String) {
        showShimmerEffect()
        mainViewModel.searchRecipes(recipesViewModel.applySearchRecipeQuery(searchQuery))
        mainViewModel.searchedRecipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    val foodRecipe = response.data
                    foodRecipe?.let { recipesAdapter.setData(it) }
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

    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.recyclerViewState =
            binding.recicpeRecyclerView.layoutManager?.onSaveInstanceState()
        _binding = null
    }

}