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
        binding.PersonalRankingImage.setOnClickListener{
            startActivity(Intent(this,PersonalRankingActivity::class.java))
        }
        binding.InGroupRankingImage.setOnClickListener{
            startActivity(Intent(this,InGroupRankingActivity::class.java))
        }
        binding.MyRankingStatsImage.setOnClickListener {
            startActivity(Intent(this,RankingStatsActivity::class.java))
        }
        binding.RankingHomeBackButton.setOnClickListener {
            super.onBackPressed()
        }
        binding.ProfileImageButton.setOnClickListener {
            startActivity(Intent(this,RankingStatsActivity::class.java))
        }
    }



}