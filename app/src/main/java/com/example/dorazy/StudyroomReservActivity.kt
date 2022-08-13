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
        var table1 = intent.getBooleanExtra("table1", false)
        var table2 = intent.getBooleanExtra("table2", false)
        var table3 = intent.getBooleanExtra("table3", false)
        var table4 = intent.getBooleanExtra("table4", false)
        var t1book: String?
        var t2book: String?
        var t3book: String?
        var t4book: String?

        // 다이얼로그에서 예를 누르는 경우
        fun reservClickYes(tc:Int) {
            isReserv = true
            when (tc) {
                1 -> {
                    table1 = true
                    t1book = auth!!.uid
                    db.collection("reservation").document("StudyRoom").update("table1", table1)
                    db.collection("reservation").document("StudyRoom").update("t1_booker", t1book)
                }
                2 -> {
                    table2 = true
                    t2book = auth!!.uid
                    db.collection("reservation").document("StudyRoom").update("table2", table2)
                    db.collection("reservation").document("StudyRoom").update("t2_booker", t2book)
                }
                3 -> {
                    table3 = true
                    t3book = auth!!.uid
                    db.collection("reservation").document("StudyRoom").update("table3", table3)
                    db.collection("reservation").document("StudyRoom").update("t3_booker", t3book)
                }
                else -> {
                    table4 = true
                    t4book = auth!!.uid
                    db.collection("reservation").document("StudyRoom").update("table4", table4)
                    db.collection("reservation").document("StudyRoom").update("t4_booker", t4book)
                }
            }
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
        if (table1)
            binding.table1.setImageResource(R.drawable.studyroom_table1_reserv)
        else
            binding.table1.setImageResource(R.drawable.studyroom_table1)

        if (table2)
            binding.table2.setImageResource(R.drawable.studyroom_table2_reserv)
        else
            binding.table2.setImageResource(R.drawable.studyroom_table2)

        if (table3)
            binding.table3.setImageResource(R.drawable.studyroom_table3_reserv)
        else
            binding.table3.setImageResource(R.drawable.studyroom_table3)

        if (table4)
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
            if (table1){
                Toast.makeText(this, "이미 예약된 자리입니다.", Toast.LENGTH_SHORT).show()
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
            if (table2){
                Toast.makeText(this, "이미 예약된 자리입니다!", Toast.LENGTH_SHORT).show()
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
            if (table3){
                Toast.makeText(this, "이미 예약된 자리입니다!", Toast.LENGTH_SHORT).show()
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
            if (table4){
                Toast.makeText(this, "이미 예약된 자리입니다!", Toast.LENGTH_SHORT).show()
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
        val timeGuide = ConfirmDialog("알림","좌석은 최대 2시간까지 사용 가능합니다.","확인")
        timeGuide.isCancelable=false
        timeGuide.show(this.supportFragmentManager,"ConfirmDialog")
    }
}
