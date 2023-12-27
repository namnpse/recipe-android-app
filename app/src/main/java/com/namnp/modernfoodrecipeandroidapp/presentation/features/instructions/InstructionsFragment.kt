package com.namnp.modernfoodrecipeandroidapp.presentation.features.instructions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.namnp.modernfoodrecipeandroidapp.data.models.Result
import com.namnp.modernfoodrecipeandroidapp.constant.Constants
import com.namnp.modernfoodrecipeandroidapp.databinding.FragmentInstructionsBinding
import com.namnp.modernfoodrecipeandroidapp.util.retrieveParcelable

class InstructionsFragment : Fragment() {

    private var _binding: FragmentInstructionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        _binding = FragmentInstructionsBinding.inflate(inflater, container, false)
        _binding = FragmentInstructionsBinding.inflate(layoutInflater)

        val args = arguments
        val bundle: Result? = args?.retrieveParcelable(Constants.RECIPE_RESULT_KEY)

        bundle?.let {
            binding.instructionsWebView.webViewClient = object : WebViewClient() {}
            val websiteUrl: String = it.sourceUrl
            binding.instructionsWebView.loadUrl(websiteUrl)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}