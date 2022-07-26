package com.example.dorazy

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.dorazy.databinding.InGroupRankingBinding


class InGroupRankingActivity :AppCompatActivity() {

    private lateinit var binding: InGroupRankingBinding
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = InGroupRankingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.InGroupRankingBackButton.setOnClickListener {
            super.onBackPressed()
        }
    }


}