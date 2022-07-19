package com.example.dorazy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dorazy.databinding.ActivityStudyroomBinding

class StudyroomActivity : AppCompatActivity() {
    private lateinit var binding:ActivityStudyroomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_studyroom)

        binding = ActivityStudyroomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.meetChangeBtn.setOnClickListener {
            startActivity(Intent(this, MeetActivity::class.java))
        }

        binding.selfstudyBtn.setOnClickListener {
            startActivity(Intent(this, selfstudyActivity::class.java))
        }
        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}