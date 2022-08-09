package com.example.dorazy

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.dorazy.databinding.ActivityMeetReservBinding


class MeetReservActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMeetReservBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMeetReservBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
            // var meetReserv = false // 인텐트하면서 변수가 초기화되는 것으로 보임

        var isReserv = intent.getBooleanExtra("isReserv", false) // default value 필요
        var chooseTable = false // 테이블 선택 여부

        var table1Click = 0 // 테이블 예약 시 누르는 클릭
        var table2Click = 0 // 테이블 예약 시 누르는 클릭
        var table3Click = 0 // 테이블 예약 시 누르는 클릭

        // 인텐트
        val meetIntent = Intent(this, MeetActivity::class.java)
        val mainIntent = Intent(this, MainActivity::class.java)
        val meetReservIntent = Intent(this, MeetReservActivity::class.java)
        val selfStudyIntent = Intent(this, SelfstudyActivity::class.java)
        val selfStudyReservIntent = Intent(this, SelfstudyReservActivity::class.java)
        val studyroomIntent = Intent(this, StudyroomActivity::class.java)
        val studyroomReservIntent = Intent(this, MeetActivity::class.java)

        // 자리
        var table1 = intent.getBooleanExtra("table1", false) // table1 자리 존재 여부
        var table2 = intent.getBooleanExtra("table2", false) // table2 자리 존재 여부
        var table3 = intent.getBooleanExtra("table3", false) // table3 자리 존재 여부


        // 다이얼로그에서 예를 누르는 경우
        fun reservClickYes() {
            isReserv = true
            meetIntent.putExtra("isReserv", isReserv)
            meetIntent.putExtra("table1", table1)
            meetIntent.putExtra("table2", table2)
            meetIntent.putExtra("table3", table3)
            startActivity(meetIntent) // 명령어
        }

        // MeetReserv에서 자리예약 진행
        fun showDialog(){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("자리 예약")
            builder.setMessage("자리를 예약하시겠습니까?")
            // 자리 예약 기능 추가
            // 버튼 글자 변경
//            var inflater:LayoutInflater = layoutInflater
//            builder.setView(inflater.inflate(R.layout.meet_reserv_dialog, null))

            var listener = DialogInterface.OnClickListener { _, p1 ->
                when(p1) {
                    DialogInterface.BUTTON_POSITIVE ->
                        reservClickYes()
                    DialogInterface.BUTTON_NEGATIVE ->
                        toast("취소하셨습니다")
                }
            }

            builder.setPositiveButton("예", listener)
            builder.setNegativeButton("아니요", listener)
            builder.show()
        }



        binding = ActivityMeetReservBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이전 화면 intent
        binding.backBtn.setOnClickListener {
            startActivity(meetIntent)
        }

        // 예약버튼 클릭
        binding.reservBtn.setOnClickListener {
            if (!isReserv and chooseTable)
                showDialog()
        }

        // 하나만 클릭이 가능하게

        // table1 클릭시 (다이얼로그에 테이블 번호 매기기)
        binding.table1.setOnClickListener {
            table1 = !table1
            table2 = false
            table3 = false
            table1Click++

            chooseTable = if (table1Click%2 == 1) // 테이블 클릭한 경우
            {
                binding.table1.setImageResource(R.drawable.meet_table1_reserv)// 예약가능 이미지로 변경
                binding.table2.setImageResource(R.drawable.meet_table2)
                binding.table3.setImageResource(R.drawable.meet_table3)
                true
            } else {
                binding.table1.setImageResource(R.drawable.meet_table1) // 예약불가 이미지로 변경
                false
            }
        }

        //table2 클릭시 (다이얼로그에 테이블 번호 매기기)
        binding.table2.setOnClickListener {
            table2 = !table2
            table1 = false // 중복 체크 불가
            table3 = false // 중복 체크 불가
            table2Click++

            chooseTable = if (table2Click%2 == 1) {
                binding.table2.setImageResource(R.drawable.meet_table2_reserv) // 예약가능 이미지로 변경
                binding.table1.setImageResource(R.drawable.meet_table1)
                binding.table3.setImageResource(R.drawable.meet_table3)
                true
            } else {
                binding.table2.setImageResource(R.drawable.meet_table2) // 예약불가 이미지로 변경
                false
            }
        }

        // table3 클릭시 (다이얼로그에 테이블 번호 매기기)
        binding.table3.setOnClickListener {
            table3 = !table3
            table1 = false
            table2 = false
            table3Click++

            chooseTable = if (table3Click%2 == 1) {
                binding.table3.setImageResource(R.drawable.meet_table3_reserv) // 예약가능 이미지로 변경
                binding.table1.setImageResource(R.drawable.meet_table1)
                binding.table2.setImageResource(R.drawable.meet_table2)
                true
            } else {
                binding.table3.setImageResource(R.drawable.meet_table3) // 예약불가 이미지로 변경
                false
            }
        }

    }

    private fun toast(message:String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}
