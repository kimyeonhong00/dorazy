package com.example.dorazy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dorazy.databinding.ActivityMeetBinding

class MeetActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMeetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meet)


        binding = ActivityMeetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.studyroomBtn.setOnClickListener {
            startActivity(Intent(this, StudyroomActivity::class.java))
        }

        binding.selfstudyBtn.setOnClickListener {
            startActivity(Intent(this, selfstudyActivity::class.java))
        }

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

}