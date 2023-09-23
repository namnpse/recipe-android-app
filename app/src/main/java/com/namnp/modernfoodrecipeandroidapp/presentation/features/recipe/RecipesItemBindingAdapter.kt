package com.namnp.modernfoodrecipeandroidapp.presentation.features.recipe

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.namnp.modernfoodrecipeandroidapp.R
import coil.load

class RecipesItemBindingAdapter {

    companion object {

        @JvmStatic
        @BindingAdapter("loadImageFromUrl")
        fun loadImageFromUrl(imageView: ImageView, imageUrl: String) {
            imageView.load(imageUrl) {
                crossfade(600)
            }
        }

        @JvmStatic
        @BindingAdapter("setNumberOfLikes")
        fun setNumberOfLikes(textView: TextView, likes: Int){
            textView.text = likes.toString()
        }

        @JvmStatic
        @BindingAdapter("setNumberOfMinutes")
        fun setNumberOfMinutes(textView: TextView, minutes: Int){
            textView.text = minutes.toString()
        }

        @JvmStatic
        @BindingAdapter("applyVeganColor")
        fun applyVeganColor(view: View, vegan: Boolean) {
            if(vegan){
                when(view){
                    is TextView -> {
                        view.setTextColor(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }
                    is ImageView -> {
                        view.setColorFilter(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }
                }
            }
        }

    }

}