package com.example.dorazy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dorazy.databinding.ActivitySelfstudyBinding

class SelfstudyActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelfstudyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selfstudy)

        binding = ActivitySelfstudyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 미팅룸 activity로 전환
        binding.meetChangeBtn.setOnClickListener {
            startActivity(Intent(this, MeetActivity::class.java))
        }

        // 스터디룸 activity로  전환
        binding.studyroomBtn.setOnClickListener {
            startActivity(Intent(this, StudyroomActivity::class.java))
        }

        // 메인으로 돌아가기 activity
        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        //예약 버튼
        binding.reservBtn.setOnClickListener {
            startActivity(Intent(this, SelfstudyReservActivity::class.java))
        }

    }
}