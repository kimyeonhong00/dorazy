package com.example.dorazy

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.dorazy.databinding.ActivityMeetBinding


class MeetActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMeetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meet)

        binding = ActivityMeetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 예약 여부
        var isReserv = intent.getBooleanExtra("isReserv", false)

        // 인텐트
        var meetIntent = Intent(this, MeetActivity::class.java)
        val mainIntent = Intent(this, MainActivity::class.java)
        val meetReservIntent = Intent(this, MeetReservActivity::class.java)
        val selfStudyIntent = Intent(this, SelfstudyActivity::class.java)
        var selfStudyReservIntent = Intent(this, SelfstudyReservActivity::class.java)
        val studyroomIntent = Intent(this, StudyroomActivity::class.java)
        var studyroomReservIntent = Intent(this, MeetActivity::class.java)

        // 자리
        var table1 = intent.getBooleanExtra("table1", false) // table1 자리 존재 여부
        var table2 = intent.getBooleanExtra("table2", false) // table2 자리 존재 여부
        var table3 = intent.getBooleanExtra("table3", false) // table3 자리 존재 여부

        // 자리 반납 기능
        fun reservCancelClickYes() {
            isReserv = false
            meetIntent.putExtra("isReserv", isReserv)

            if (table1 == true)
                table1 = false
            if (table2 == true)
                table2 = false
            if (table3 == true)
                table3 = false

            startActivity(meetIntent)

        }

        //예약 기능을 반영한 테이블 img
        if (table1)
            binding.table1.setImageResource(R.drawable.meet_table1_reserv)
        else
            binding.table1.setImageResource(R.drawable.meet_table1)

        if (table2)
            binding.table2.setImageResource(R.drawable.meet_table2_reserv)
        else
            binding.table2.setImageResource(R.drawable.meet_table2)

        if (table3)
            binding.table3.setImageResource(R.drawable.meet_table3_reserv)
        else
            binding.table3.setImageResource(R.drawable.meet_table3)




        // 자리 반납
        fun showDialog() {
            val builder2 = AlertDialog.Builder(this)
            builder2.setTitle("자리 반납")
            builder2.setMessage("자리를 반납하시겠습니까?")


            // 버튼 글자 변경

            var listener = DialogInterface.OnClickListener { _, p1 ->
                when(p1) {
                    DialogInterface.BUTTON_POSITIVE ->
                        reservCancelClickYes() // 명령어

                    DialogInterface.BUTTON_NEGATIVE ->
                        toast("취소하셨습니다")
                }
            }

            builder2.setPositiveButton("예", listener)
            builder2.setNegativeButton("아니요", listener)
            builder2.show()
        }

        binding.studyroomBtn.setOnClickListener {
            startActivity(studyroomIntent)
        }

        binding.selfstudyBtn.setOnClickListener {
            startActivity(selfStudyIntent)
        }

        binding.backBtn.setOnClickListener {
            mainIntent.putExtra("isReserv", isReserv)
            startActivity(mainIntent)
        }

        // 자리를 예약한 경우
        if (isReserv)
            binding.reservBtn.text = "자리 반납"

        // 예약 버튼 관련
        binding.reservBtn.setOnClickListener {
            if (!isReserv)
            {
                meetReservIntent.putExtra("isReserv", isReserv)
                startActivity(meetReservIntent)
            }
            else
            {
                showDialog()
            }

        }
        // 예약 기능 추가
    }

    fun toast(message:String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}