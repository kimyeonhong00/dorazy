package com.example.dorazy

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.dorazy.databinding.ActivitySelfstudyReservBinding

class SelfstudyReservActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelfstudyReservBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selfstudy)

        fun showDialog() {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
//                .setTitle("예약")
//                .setMessage("예약하시겠습니까?")
//                .setPositiveButton("예", positiveButtonClick)
//                .setNegativeButton("아니요", negativeButtonClick)
//                .show()
            builder.setPositiveButton("네") {
                p0, p1 -> startActivity(Intent(this, SelfstudyActivity::class.java))
            }



            // 팝업에서 예를 선택한 경우
//            val positiveButtonClick = {
//                    dialogInterface: DialogInterface, i: Int ->
//                // 명령어
//                startActivity(Intent(this, SelfstudyActivity::class.java))
//            }
//            val negativeButtonClick = {
//                    dialogInterface: DialogInterface, i: Int ->
//                toast("취소하셨습니다")
//            }
        }

        binding = ActivitySelfstudyReservBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 미팅룸 예약 activity로 전환
        binding.meetChangeBtn.setOnClickListener {
            startActivity(Intent(this, MeetReservActivity::class.java))
        }

        // 스터디룸 예약 activity로 전환
        binding.studyroomBtn.setOnClickListener {
            startActivity(Intent(this, StudyroomReservActivity::class.java))
        }

        // 자율학습 activity로
        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, SelfstudyActivity::class.java))
        }

        // 예약 기능
        binding.reservBtn.setOnClickListener {
           showDialog()
        }


    }

    fun toast(message:String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}