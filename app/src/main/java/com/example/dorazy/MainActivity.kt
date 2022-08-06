package com.example.dorazy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dorazy.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private var auth :FirebaseAuth? = null
    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.rankingbutton.setOnClickListener {
            startActivity(Intent(this, PersonalRankingActivity::class.java))
        }

        binding.recordbutton.setOnClickListener{
            auth!!.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}