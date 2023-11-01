package com.namnp.modernfoodrecipeandroidapp.presentation.features.ingredients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.namnp.modernfoodrecipeandroidapp.data.models.Result
import androidx.recyclerview.widget.LinearLayoutManager
import com.namnp.modernfoodrecipeandroidapp.R
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.RECIPE_RESULT_KEY
import kotlinx.android.synthetic.main.fragment_ingredients.view.*

class IngredientsFragment : Fragment() {

    private val ingredientsAdapter: IngredientsAdapter by lazy { IngredientsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ingredients, container, false)
        val args = arguments
        val recipeBundle: Result? = args?.getParcelable(RECIPE_RESULT_KEY)
        setupRecyclerView(view)
        recipeBundle?.extendedIngredients?.let { ingredientsAdapter.setData(it) }

        return view
    }

    private fun setupRecyclerView(view: View) {
        view.ingredients_recyclerview.adapter = ingredientsAdapter
        view.ingredients_recyclerview.layoutManager = LinearLayoutManager(requireContext())
    }

}