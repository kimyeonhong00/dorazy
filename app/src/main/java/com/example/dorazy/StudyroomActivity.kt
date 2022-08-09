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

        // 미팅 전환
        binding.meetChangeBtn.setOnClickListener {
            startActivity(Intent(this, MeetActivity::class.java))
        }

        // 자율학습 전환
        binding.selfstudyBtn.setOnClickListener {
            startActivity(Intent(this, SelfstudyActivity::class.java))
        }

        // 메인으로 전환
        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // 예약
        binding.reservBtn.setOnClickListener {
            startActivity(Intent(this, StudyroomReservActivity::class.java))
        }
    }
}