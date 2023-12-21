package com.example.movies

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.movies.Home.HomeFragment
import com.example.movies.Pesanan.PesananFragment
import com.example.movies.Profile.ProfileFragment
import com.example.movies.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("account_data", MODE_PRIVATE)
        val nama = sharedPreferences.getString("USER_NAMA", null)

        var userNama = ""
        if (nama != null) {
            userNama = nama
        }

        val homeFragment = HomeFragment()
        val bundle = Bundle()
        bundle.putString("USER_NAMA", userNama)
        homeFragment.arguments = bundle

        replaceFragment(homeFragment)
        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nav_home -> replaceFragment(homeFragment)
                R.id.nav_pesanan -> replaceFragment(PesananFragment())
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