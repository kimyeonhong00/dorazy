package com.example.dorazy

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.dorazy.databinding.ActivityStudyroomReservBinding

class StudyroomReservActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudyroomReservBinding

    // 팝업에서 예를 선택한 경우
    val positiveButtonClick = {
            dialogInterface: DialogInterface, i: Int ->
        // 명령어
        startActivity(Intent(this, StudyroomActivity::class.java))
    }
    val negativeButtonClick = {
            dialogInterface: DialogInterface, i: Int ->
        toast("취소하셨습니다")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_studyroom)

        binding = ActivityStudyroomReservBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 미팅 전환
        binding.meetChangeBtn.setOnClickListener {
            startActivity(Intent(this, MeetReservActivity::class.java))
        }

        // 자율학습 전환
        binding.selfstudyBtn.setOnClickListener {
            startActivity(Intent(this, SelfstudyReservActivity::class.java))
        }

        // 예약 전 화면 전환
        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, StudyroomActivity::class.java))
        }

        // 예약 intent
        binding.reservBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
                .setTitle("예약")
                .setMessage("예약하시겠습니까?")
                .setPositiveButton("예", positiveButtonClick)
                .setNegativeButton("아니요", negativeButtonClick)
                .show()
        }
    }

        fun toast(message:String){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
}