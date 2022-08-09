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
    private val positiveButtonClick = {
            dialogInterface: DialogInterface, i: Int ->
        // 명령어
        startActivity(Intent(this, StudyroomActivity::class.java))
    }
    private val negativeButtonClick = {
            dialogInterface: DialogInterface, i: Int ->
        toast("취소하셨습니다")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityStudyroomReservBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var isReserv = intent.getBooleanExtra("isReserv", false) // default value 필요
        var chooseTable = false // 테이블 선택 여부

        var table1Click = 0 // 테이블 예약 시 누르는 클릭
        var table2Click = 0
        var table3Click = 0
        var table4Click = 0

        var table1 = intent.getBooleanExtra("table1", false)
        var table2 = intent.getBooleanExtra("table2", false)
        var table3 = intent.getBooleanExtra("table3", false)
        var table4 = intent.getBooleanExtra("table3", false)

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

        // table1 클릭시 (다이얼로그에 테이블 번호 매기기)
        binding.table1.setOnClickListener {
            table1 = !table1
            table2 = false
            table3 = false
            table4 = false
            table1Click++

            chooseTable = if (table1Click%2 == 1) // 테이블 클릭한 경우
            {
                binding.table1.setImageResource(R.drawable.studyroom_table1_reserv)// 예약가능 이미지로 변경
                binding.table2.setImageResource(R.drawable.studyroom_table2)
                binding.table3.setImageResource(R.drawable.studyroom_table3)
                binding.table4.setImageResource(R.drawable.studyroom_table4)
                true
            } else {
                binding.table1.setImageResource(R.drawable.studyroom_table1) // 예약불가 이미지로 변경
                false
            }
        }

        //table2 클릭시 (다이얼로그에 테이블 번호 매기기)
        binding.table2.setOnClickListener {
            table2 = !table2
            table1 = false
            table3 = false
            table4 = false
            table2Click++

            chooseTable = if (table2Click%2 == 1) {
                binding.table2.setImageResource(R.drawable.studyroom_table2_reserv) // 예약가능 이미지로 변경
                binding.table1.setImageResource(R.drawable.studyroom_table1)
                binding.table3.setImageResource(R.drawable.studyroom_table3)
                binding.table4.setImageResource(R.drawable.studyroom_table4)
                true
            } else {
                binding.table2.setImageResource(R.drawable.studyroom_table2) // 예약불가 이미지로 변경
                false
            }
        }

        // table3 클릭시 (다이얼로그에 테이블 번호 매기기)
        binding.table3.setOnClickListener {
            table3 = !table3
            table1 = false
            table2 = false
            table4 = false
            table3Click++

            chooseTable = if (table3Click%2 == 1) {
                binding.table3.setImageResource(R.drawable.studyroom_table3_reserv) // 예약가능 이미지로 변경
                binding.table1.setImageResource(R.drawable.studyroom_table1)
                binding.table2.setImageResource(R.drawable.studyroom_table2)
                binding.table4.setImageResource(R.drawable.studyroom_table4)
                true
            } else {
                binding.table3.setImageResource(R.drawable.studyroom_table3) // 예약불가 이미지로 변경
                false
            }
        }

        // table4 클릭시 (다이얼로그에 테이블 번호 매기기)
        binding.table4.setOnClickListener {
            table4 = !table4
            table1 = false
            table2 = false
            table3 = false
            table4Click++

            chooseTable = if (table4Click%2 == 1) {
                binding.table4.setImageResource(R.drawable.studyroom_table4_reserv) // 예약가능 이미지로 변경
                binding.table1.setImageResource(R.drawable.studyroom_table1)
                binding.table2.setImageResource(R.drawable.studyroom_table2)
                binding.table3.setImageResource(R.drawable.studyroom_table3)
                true
            } else {
                binding.table4.setImageResource(R.drawable.studyroom_table4) // 예약불가 이미지로 변경
                false
            }
        }
    }



        fun toast(message:String){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
}