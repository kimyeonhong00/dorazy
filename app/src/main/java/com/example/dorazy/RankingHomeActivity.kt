package com.example.dorazy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dorazy.databinding.RankingHomeBinding

class RankingHomeActivity :AppCompatActivity() {

    private lateinit var binding: RankingHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RankingHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.WeeklyRankingImage.setOnClickListener{
            startActivity(Intent(this,WeeklyRankingActivity::class.java))
        }
    }



}