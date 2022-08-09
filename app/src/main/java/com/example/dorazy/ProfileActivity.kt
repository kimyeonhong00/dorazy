package com.example.dorazy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.TextView
import com.example.dorazy.databinding.ActivityProfileBinding


class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var userStudyTime = 0
        setContentView(R.layout.activity_profile)

        // 바인딩 초기화(코틀린 파일에서 값 수정하기 위해 필요)
        binding = ActivityProfileBinding.inflate(layoutInflater)

        // text를 메인에서 받아 text 변경하기(%는 유지되어야 함)
        binding.percent.text = "${userStudyTime.toString()}%"




        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }
}