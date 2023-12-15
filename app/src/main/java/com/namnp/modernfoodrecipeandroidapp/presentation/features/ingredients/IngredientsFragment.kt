package com.namnp.modernfoodrecipeandroidapp.presentation.features.ingredients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namnp.modernfoodrecipeandroidapp.data.models.Result
import androidx.recyclerview.widget.LinearLayoutManager
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.RECIPE_RESULT_KEY
import com.namnp.modernfoodrecipeandroidapp.databinding.FragmentIngredientsBinding
import com.namnp.modernfoodrecipeandroidapp.util.retrieveParcelable

class IngredientsFragment : Fragment() {

    private val ingredientsAdapter: IngredientsAdapter by lazy { IngredientsAdapter() }
    private var _binding: FragmentIngredientsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentIngredientsBinding.inflate(layoutInflater)
        val recipeBundle: Result? = arguments?.retrieveParcelable(RECIPE_RESULT_KEY)
        setupRecyclerView()
        recipeBundle?.extendedIngredients?.let { ingredientsAdapter.setData(it) }

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.ingredientsRecyclerview.apply {
            adapter = ingredientsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
//        with(binding.ingredientsRecyclerview) {
//            adapter = ingredientsAdapter
//            layoutManager = LinearLayoutManager(requireContext())
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}