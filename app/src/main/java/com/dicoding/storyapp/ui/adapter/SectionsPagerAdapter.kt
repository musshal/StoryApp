package com.dicoding.storyapp.ui.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dicoding.storyapp.ui.home.BookmarkFragment
import com.dicoding.storyapp.ui.home.HomeFragment

class SectionsPagerAdapter internal constructor(
    activity: AppCompatActivity
    ) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null

        when (position) {
            0 -> fragment = HomeFragment()
            1 -> fragment = BookmarkFragment()
        }

        return fragment as Fragment
    }

    override fun getItemCount(): Int = 2
}