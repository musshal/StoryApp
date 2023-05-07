package com.dicoding.storyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dicoding.storyapp.ui.home.HomeFragment
import com.dicoding.storyapp.ui.insert.InsertFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentManager = supportFragmentManager
        val homeFragment = HomeFragment()
        val insertFragment = InsertFragment()
        val fragment = fragmentManager.findFragmentByTag(InsertFragment::class.java.simpleName)

        if (fragment !is InsertFragment) {
            Log.d("StoryApp", "Fragment Name: " + InsertFragment::class.java.simpleName)
            fragmentManager
                .beginTransaction()
                .add(R.id.frame_container, insertFragment, InsertFragment::class.java.simpleName)
                .commit()
        }
    }
}