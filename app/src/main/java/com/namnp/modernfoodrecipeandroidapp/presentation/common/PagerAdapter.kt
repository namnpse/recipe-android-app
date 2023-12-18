package com.namnp.modernfoodrecipeandroidapp.presentation.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.*

class PagerAdapter(
    private val resultBundle: Bundle,
    private val fragments: ArrayList<Fragment>,
    private val fragmentActivity: FragmentActivity,
): FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment {
        fragments[position].arguments = resultBundle
        return fragments[position]
    }
}