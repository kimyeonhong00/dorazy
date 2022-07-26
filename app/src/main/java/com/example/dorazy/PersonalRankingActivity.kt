package com.example.dorazy

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.dorazy.databinding.PersonalRankingBinding


class PersonalRankingActivity :AppCompatActivity() {

    private lateinit var binding: PersonalRankingBinding
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PersonalRankingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.PersonalRankingBackButton.setOnClickListener {
            super.onBackPressed()
        }
    }


}