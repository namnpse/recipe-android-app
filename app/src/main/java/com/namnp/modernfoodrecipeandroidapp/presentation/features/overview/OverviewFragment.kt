package com.namnp.modernfoodrecipeandroidapp.presentation.features.overview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import coil.load
import com.namnp.modernfoodrecipeandroidapp.R
import com.namnp.modernfoodrecipeandroidapp.constant.Constants.Companion.RECIPE_RESULT_KEY
import com.namnp.modernfoodrecipeandroidapp.data.models.Result
import com.namnp.modernfoodrecipeandroidapp.databinding.FragmentOverviewBinding
import com.namnp.modernfoodrecipeandroidapp.presentation.features.recipe.RecipesItemBindingAdapter
import com.namnp.modernfoodrecipeandroidapp.util.retrieveParcelable
import org.jsoup.Jsoup

class OverviewFragment : Fragment() {

    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)

        val args = arguments
        val bundle: Result? = args?.retrieveParcelable(RECIPE_RESULT_KEY)

        bundle?.let {
            binding.mainImageView.load(it.image)
            binding.titleTextView.text = it.title
            binding.likesTextView.text = it.aggregateLikes.toString()
            binding.timeTextView.text = it.readyInMinutes.toString()

            RecipesItemBindingAdapter.parseHtml(binding.summaryTextView, it.summary)

            updateColors(it.vegetarian, binding.vegetarianTextView, binding.vegetarianImageView)
            updateColors(it.vegan, binding.veganTextView, binding.veganImageView)
            updateColors(it.cheap, binding.cheapTextView, binding.cheapImageView)
            updateColors(it.dairyFree, binding.dairyFreeTextView, binding.dairyFreeImageView)
            updateColors(it.glutenFree, binding.glutenFreeTextView, binding.glutenFreeImageView)
            updateColors(it.veryHealthy, binding.healthyTextView, binding.healthyImageView)
        }

        return binding.root
    }

    private fun updateColors(isSelected: Boolean, textView: TextView, imageView: ImageView) {
        if (isSelected) {
            imageView.setColorFilter(ContextCompat.getColor(requireContext(),R.color.green))
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}