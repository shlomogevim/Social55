package com.sg.social55.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sg.social55.R
import com.sg.social55.databinding.ActivityMainBinding
import com.sg.social55.fragments.HomeFragment
import com.sg.social55.fragments.NotificationFragment
import com.sg.social55.fragments.ProfileFragment
import com.sg.social55.fragments.SearchFragment

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    moveToFragment(HomeFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_search -> {
                    moveToFragment(SearchFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_add_post -> {
                    item.isChecked=false
                     startActivity(Intent(this, AddPostActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_notification -> {
                   moveToFragment(NotificationFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_profile -> {
                    moveToFragment(ProfileFragment())
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        moveToFragment(HomeFragment())

       onBackPressedDispatcher?.addCallback(this,object :OnBackPressedCallback(true){
           override fun handleOnBackPressed() {
               moveToFragment(HomeFragment())
           }
       })


    }

  /*  override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }*/

    private fun moveToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .addToBackStack("popy")
            .replace(R.id.fragment_container, fragment!!)
            //.addToBackStack("tag")
            .commit()
    }






}