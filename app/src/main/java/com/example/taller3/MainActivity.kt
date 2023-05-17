package com.example.taller3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.taller3.data.LoginDataSource
import com.example.taller3.data.LoginRepository
import com.example.taller3.databinding.ActivityMainBinding
import com.example.taller3.ui.login.LoginActivity
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        binding.Navbar.setOnClickListener {
            startActivity(Intent(baseContext,LoginActivity::class.java))
        }
    }
}