package com.example.dorazy

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.dorazy.databinding.ActivityStudyroomReservBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class StudyroomReservActivity : AppCompatActivity() {

    private lateinit var binding:ActivityStudyroomReservBinding
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth : FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyroomReservBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        //이용 안내
        clickViewEvents()

        var isReserv = intent.getBooleanExtra("isReserv", false) // 예약 했는가
        var chooseTable = false // 테이블 선택 여부
        var tableClick = 0 // 선택한 테이블 번호

        // 인텐트
        val studyroomIntent = Intent(this, StudyroomActivity::class.java)

        // 자리 예약 여부
        var table1 = intent.getIntExtra("table1",0)
        var table2 = intent.getIntExtra("table2",0)
        var table3 = intent.getIntExtra("table3",0)
        var table4 = intent.getIntExtra("table4",0)
        var t1book = mutableListOf("","","","")
        var t2book = mutableListOf("","","","")
        var t3book = mutableListOf("","","","")
        var t4book = mutableListOf("","","","")
        var t1time = mutableListOf("","","","")
        var t2time = mutableListOf("","","","")
        var t3time = mutableListOf("","","","")
        var t4time = mutableListOf("","","","")
        val ind = intent.getIntExtra("ind",0)
        var t: String

        db.collection("reservation").document("StudyRoom").get().addOnSuccessListener { doc ->
            val removeChars = "[] "
            var str = doc["t1_booker"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            t1book = str.split(",").toMutableList()
            str = doc["t2_booker"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            t2book = str.split(",").toMutableList()
            str = doc["t3_booker"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            t3book = str.split(",").toMutableList()
            str = doc["t4_booker"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            t4book = str.split(",").toMutableList()
            str = doc["t1_time"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            t1time = str.split(",").toMutableList()
            str = doc["t2_time"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            t2time = str.split(",").toMutableList()
            str = doc["t3_time"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            t3time = str.split(",").toMutableList()
            str = doc["t4_time"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            t4time = str.split(",").toMutableList()
        }


        // 다이얼로그에서 예를 누르는 경우
        fun reservClickYes(tc:Int) {
            isReserv = true
            var table = 0
            var tbook = mutableListOf("","","","")
            var tTime = mutableListOf("","","","")
            when (tc) {
                1 -> {
                    table = ++table1
                    t1book[ind] = auth!!.uid.toString()
                    tbook = t1book
                    tTime = t1time
                }
                2 -> {
                    table = ++table2
                    t2book[ind] = auth!!.uid.toString()
                    tbook = t2book
                    tTime = t2time
                }
                3 -> {
                    table = ++table3
                    t3book[ind] = auth!!.uid.toString()
                    tbook = t3book
                    tTime = t3time
                }
                else -> {
                    table = ++table4
                    t4book[ind] = auth!!.uid.toString()
                    tbook = t4book
                    tTime = t4time
                }
            }
            val cur = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("ddHHmm")
            t = cur.format(formatter)
            tTime[ind] = t
            db.collection("reservation").document("StudyRoom").update("t${tc}_time", tTime)
            db.collection("reservation").document("StudyRoom").update("table$tc", table)
            db.collection("reservation").document("StudyRoom").update("t${tc}_booker", tbook)
            studyroomIntent.putExtra("isReserv", isReserv)
            startActivity(studyroomIntent) // 명령어
            finish()
        }

        // MeetReserv에서 자리예약 진행
        fun showDialog(tc : Int){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("자리 예약")
            builder.setMessage("${tc}번 자리를 예약하시겠습니까?")

            val listener = DialogInterface.OnClickListener { _, p1 ->
                when(p1) {
                    DialogInterface.BUTTON_POSITIVE ->
                        reservClickYes(tc)
                    DialogInterface.BUTTON_NEGATIVE ->
                        Toast.makeText(this, "취소하셨습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setPositiveButton("예", listener)
            builder.setNegativeButton("아니요", listener)
            builder.show()
        }

        //테이블 초기 상태 세팅
        if (table1>3)
            binding.table1.setImageResource(R.drawable.studyroom_table1_reserv)
        else
            binding.table1.setImageResource(R.drawable.studyroom_table1)

        if (table2>3)
            binding.table2.setImageResource(R.drawable.studyroom_table2_reserv)
        else
            binding.table2.setImageResource(R.drawable.studyroom_table2)

        if (table3>3)
            binding.table3.setImageResource(R.drawable.studyroom_table3_reserv)
        else
            binding.table3.setImageResource(R.drawable.studyroom_table3)

        if (table4>3)
            binding.table4.setImageResource(R.drawable.studyroom_table4_reserv)
        else
            binding.table4.setImageResource(R.drawable.studyroom_table4)

        // 이전 화면 intent
        binding.backBtn.setOnClickListener { super.onBackPressed() }

        // 예약버튼 클릭
        binding.reservBtn.setOnClickListener {
            if (!isReserv and chooseTable)
                showDialog(tableClick)
        }

        // 하나만 클릭이 가능
        // table 클릭시 (예약 가능 자리인지 확인 후 색 변화)
        binding.table1.setOnClickListener {
            if (table1>3){
                Toast.makeText(this, "자리가 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            tableClick = if (tableClick!=1) {
                chooseTable = true
                binding.table1.setImageResource(R.drawable.studyroom_table1_reserv)
                binding.reservBtn.setBackgroundColor(Color.parseColor("#002244"))
                when (tableClick) {
                    2 -> {
                        binding.table2.setImageResource(R.drawable.studyroom_table2)
                    }
                    3 -> {
                        binding.table3.setImageResource(R.drawable.studyroom_table3)
                    }
                    4 -> {
                        binding.table4.setImageResource(R.drawable.studyroom_table4)
                    }
                }
                1
            }else{
                chooseTable = false
                binding.table1.setImageResource(R.drawable.studyroom_table1)
                binding.reservBtn.setBackgroundColor(Color.parseColor("#808080"))
                0
            }
        }

        binding.table2.setOnClickListener {
            if (table2>3){
                Toast.makeText(this, "자리가 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            tableClick = if (tableClick!=2) {
                chooseTable = true
                binding.table2.setImageResource(R.drawable.studyroom_table2_reserv)
                binding.reservBtn.setBackgroundColor(Color.parseColor("#002244"))
                when (tableClick) {
                    1 -> {
                        binding.table1.setImageResource(R.drawable.studyroom_table1)
                    }
                    3 -> {
                        binding.table3.setImageResource(R.drawable.studyroom_table3)
                    }
                    4 -> {
                        binding.table4.setImageResource(R.drawable.studyroom_table4)
                    }
                }
                2
            }else{
                chooseTable = false
                binding.table2.setImageResource(R.drawable.studyroom_table2)
                binding.reservBtn.setBackgroundColor(Color.parseColor("#808080"))
                0
            }
        }

        binding.table3.setOnClickListener {
            if (table3>3){
                Toast.makeText(this, "자리가 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            tableClick = if (tableClick!=3) {
                chooseTable = true
                binding.table3.setImageResource(R.drawable.studyroom_table3_reserv)
                binding.reservBtn.setBackgroundColor(Color.parseColor("#002244"))
                when (tableClick) {
                    2 -> {
                        binding.table2.setImageResource(R.drawable.studyroom_table2)
                    }
                    1 -> {
                        binding.table1.setImageResource(R.drawable.studyroom_table1)
                    }
                    4 -> {
                        binding.table4.setImageResource(R.drawable.studyroom_table4)
                    }
                }
                3
            }else{
                chooseTable = false
                binding.table3.setImageResource(R.drawable.studyroom_table3)
                binding.reservBtn.setBackgroundColor(Color.parseColor("#808080"))
                0
            }
        }


        binding.table4.setOnClickListener {
            if (table4>3){
                Toast.makeText(this, "자리가 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            tableClick = if (tableClick!=4) {
                chooseTable = true
                binding.table4.setImageResource(R.drawable.studyroom_table4_reserv)
                binding.reservBtn.setBackgroundColor(Color.parseColor("#002244"))
                when (tableClick) {
                    1 -> {
                        binding.table1.setImageResource(R.drawable.studyroom_table1)
                    }
                    2 -> {
                        binding.table2.setImageResource(R.drawable.studyroom_table2)
                    }
                    3 -> {
                        binding.table3.setImageResource(R.drawable.studyroom_table3)
                    }
                }
                4
            }else{
                chooseTable = false
                binding.table4.setImageResource(R.drawable.studyroom_table4)
                binding.reservBtn.setBackgroundColor(Color.parseColor("#808080"))
                0
            }
        }

    }

    private fun clickViewEvents(){
        val timeGuide = ConfirmDialog("알림","좌석은 최대 2시간까지\n 사용 가능합니다.","확인")
        timeGuide.isCancelable=false
        timeGuide.show(this.supportFragmentManager,"ConfirmDialog")
    }

}
