package com.example.movies.LoginRegister

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.movies.databinding.ActivityLoginRegisterBinding

class LoginRegister : AppCompatActivity() {
    private lateinit var binding: ActivityLoginRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            viewPager.adapter = TabAdapter(supportFragmentManager)

            tabLayout.setupWithViewPager(viewPager)
        }
    }
}