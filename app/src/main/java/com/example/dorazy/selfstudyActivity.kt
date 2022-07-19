package com.example.dorazy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dorazy.databinding.ActivitySelfstudyBinding

class selfstudyActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelfstudyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selfstudy)

        binding = ActivitySelfstudyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.meetChangeBtn.setOnClickListener {
            startActivity(Intent(this, MeetActivity::class.java))
        }
        binding.studyroomBtn.setOnClickListener {
            startActivity(Intent(this, StudyroomActivity::class.java))
        }
        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}