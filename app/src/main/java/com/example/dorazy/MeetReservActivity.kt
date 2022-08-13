package com.example.dorazy

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.dorazy.databinding.ActivityMeetReservBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class MeetReservActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMeetReservBinding
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth : FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMeetReservBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = Firebase.auth

        //이용 안내
        clickViewEvents()

        var isReserv = intent.getBooleanExtra("isReserv", false) // 예약 했는가
        var chooseTable = false // 테이블 선택 여부
        var tableClick = 0 // 선택한 테이블 번호

        // 인텐트
        val meetIntent = Intent(this, MeetActivity::class.java)

        // 자리 예약 여부
        var table1 = intent.getBooleanExtra("table1", false)
        var table2 = intent.getBooleanExtra("table2", false)
        var table3 = intent.getBooleanExtra("table3", false)
        var t1book: String?
        var t2book: String?
        var t3book: String?

        // 다이얼로그에서 예를 누르는 경우
        fun reservClickYes(tc:Int) {
            isReserv = true
            when (tc) {
                1 -> {
                    table1 = true
                    t1book = auth!!.uid
                    db.collection("reservation").document("InterviewRoom").update("table1", table1)
                    db.collection("reservation").document("InterviewRoom").update("t1_booker", t1book)
                }
                2 -> {
                    table2 = true
                    t2book = auth!!.uid
                    db.collection("reservation").document("InterviewRoom").update("table2", table2)
                    db.collection("reservation").document("InterviewRoom").update("t2_booker", t2book)
                }
                else -> {
                    table3 = true
                    t3book = auth!!.uid
                    db.collection("reservation").document("InterviewRoom").update("table3", table3)
                    db.collection("reservation").document("InterviewRoom").update("t3_booker", t3book)
                }
            }
            meetIntent.putExtra("isReserv", isReserv)
            startActivity(meetIntent) // 명령어
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
                binding.table1.setImageResource(R.drawable.meet_table1_reserv)
                binding.reservBtn.setBackgroundColor(Color.parseColor("#002244"))
                if (tableClick==2){
                    binding.table2.setImageResource(R.drawable.meet_table2)
                } else if (tableClick==3){
                    binding.table3.setImageResource(R.drawable.meet_table3)
                }
                1
            }else{
                chooseTable = false
                binding.table1.setImageResource(R.drawable.meet_table1)
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
                binding.table2.setImageResource(R.drawable.meet_table2_reserv)
                binding.reservBtn.setBackgroundColor(Color.parseColor("#002244"))
                if (tableClick==1){
                    binding.table1.setImageResource(R.drawable.meet_table1)
                } else if (tableClick==3){
                    binding.table3.setImageResource(R.drawable.meet_table3)
                }
                2
            }else{
                chooseTable = false
                binding.table2.setImageResource(R.drawable.meet_table2)
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
                binding.table3.setImageResource(R.drawable.meet_table3_reserv)
                binding.reservBtn.setBackgroundColor(Color.parseColor("#002244"))
                if (tableClick==2){
                    binding.table2.setImageResource(R.drawable.meet_table2)
                } else if (tableClick==1){
                    binding.table1.setImageResource(R.drawable.meet_table1)
                }
                3
            }else{
                chooseTable = false
                binding.table3.setImageResource(R.drawable.meet_table3)
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
