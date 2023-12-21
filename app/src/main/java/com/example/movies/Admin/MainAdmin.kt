package com.example.movies.Admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.movies.Admin.Home.HomeAdminFragment
import com.example.movies.Profile.ProfileFragment
import com.example.movies.R
import com.example.movies.databinding.ActivityMainAdminBinding

class MainAdmin : AppCompatActivity() {
    private lateinit var binding: ActivityMainAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(HomeAdminFragment())
        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_home -> replaceFragment(HomeAdminFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())

                else -> {}
            }
            true
        }
    }

    private fun replaceFragment(fragment : Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}