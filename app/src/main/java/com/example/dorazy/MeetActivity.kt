package com.example.dorazy

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.dorazy.databinding.ActivityMeetBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.concurrent.thread


class MeetActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMeetBinding
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth : FirebaseAuth? = null
    private var timerTask : Timer? = null
    private var timeUsage = 0
    private var reservedTable = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        // 예약 여부
        var isReserv = intent.getBooleanExtra("isReserv", false)

        // 인텐트
        val meetIntent = Intent(this, MeetActivity::class.java)
        val mainIntent = Intent(this, MainActivity::class.java)
        val meetReservIntent = Intent(this, MeetReservActivity::class.java)
        val selfStudyIntent = Intent(this, SelfstudyActivity::class.java)
        val studyroomIntent = Intent(this, StudyroomActivity::class.java)

        // 현재 자리 데이터 불러오기
        binding.reservBtn.isEnabled=false
        var table1 = false
        var table2 = false
        var table3 = false
        var t1book:String?=null
        var t2book:String?=null
        var t3book:String?=null
        var t = 0
        db.collection("reservation").document("InterviewRoom").get().addOnSuccessListener {
            table1 = it["table1"].toString().toBoolean()
            table2 = it["table2"].toString().toBoolean()
            table3 = it["table3"].toString().toBoolean()
            t1book = it["t1_booker"].toString()
            t2book = it["t2_booker"].toString()
            t3book = it["t3_booker"].toString()
            meetReservIntent.putExtra("table1",table1)
            meetReservIntent.putExtra("table2",table2)
            meetReservIntent.putExtra("table3",table3)
            meetReservIntent.putExtra("t1book",t1book)
            meetReservIntent.putExtra("t2book",t2book)
            meetReservIntent.putExtra("t3book",t3book)
            if (t1book==auth!!.uid.toString() || t2book==auth!!.uid.toString() || t3book==auth!!.uid.toString()){
                isReserv = true
                t = if (t1book==auth!!.uid.toString()){
                    reservedTable = 1
                    it["t1_time"].toString().toInt()
                } else if (t2book==auth!!.uid.toString()){
                    reservedTable = 2
                    it["t2_time"].toString().toInt()
                } else {
                    reservedTable = 3
                    it["t3_time"].toString().toInt()
                }
            }
        }


        // 자리 반납 기능
        fun reservCancelClickYes() {
            isReserv = false
            when (auth!!.uid.toString()) {
                t1book -> {
                    table1 = false; t1book = null
                    db.collection("reservation").document("InterviewRoom").update("table1",table1)
                    db.collection("reservation").document("InterviewRoom").update("t1_booker",t1book)
                    db.collection("reservation").document("InterviewRoom").update("t1_time",0)
                }
                t2book -> {
                    table2 = false; t2book = null
                    db.collection("reservation").document("InterviewRoom").update("table2",table2)
                    db.collection("reservation").document("InterviewRoom").update("t2_booker",t2book)
                    db.collection("reservation").document("InterviewRoom").update("t2_time",0)
                }
                else -> {
                    table3 = false; t3book = null
                    db.collection("reservation").document("InterviewRoom").update("table3",table3)
                    db.collection("reservation").document("InterviewRoom").update("t3_booker",t3book)
                    db.collection("reservation").document("InterviewRoom").update("t3_time",0)
                }
            }
            meetIntent.putExtra("isReserv", isReserv)
            meetIntent.putExtra("table1", table1)
            meetIntent.putExtra("table2", table2)
            meetIntent.putExtra("table3", table3)
            startActivity(meetIntent)
        }

        //예약 기능을 반영한 테이블 img
        thread (start = true){
            Thread.sleep(1500)
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
            if (isReserv) {
                // 2시간 타이머
                runTimer(t, reservedTable)
                binding.reservBtn.text = getString(R.string.return_seat)
            }
            runOnUiThread {
                binding.reservBtn.isEnabled = true
            }
        }


        // 자리 반납
        fun showDialog() {
            val builder2 = AlertDialog.Builder(this)
            builder2.setTitle("자리 반납")
            builder2.setMessage("자리를 반납하시겠습니까?")

            val listener = DialogInterface.OnClickListener { _, p1 ->
                when(p1) {
                    DialogInterface.BUTTON_POSITIVE ->
                        reservCancelClickYes() // 명령어

                    DialogInterface.BUTTON_NEGATIVE ->
                        Toast.makeText(this, "취소하셨습니다", Toast.LENGTH_SHORT).show()
                }
            }

            builder2.setPositiveButton("예", listener)
            builder2.setNegativeButton("아니요", listener)
            builder2.show()
        }

        binding.studyroomBtn.setOnClickListener {
            studyroomIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(studyroomIntent)
        }

        binding.selfstudyBtn.setOnClickListener {
            selfStudyIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(selfStudyIntent)
        }

        binding.backBtn.setOnClickListener {
            mainIntent.putExtra("isReserv", isReserv)
            startActivity(mainIntent)
            finish()
        }

        // 예약 or 반납 버튼
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

    private fun runTimer(t:Int,reservedTable:Int){
        timeUsage = t
        timerTask = kotlin.concurrent.timer(period = 60000) {
            timeUsage++
            when (reservedTable) {
                1 -> {
                    db.collection("reservation").document("InterviewRoom").update("t1_time",timeUsage)
                }
                2 -> {
                    db.collection("reservation").document("InterviewRoom").update("t2_time",timeUsage)
                }
                else -> {
                    db.collection("reservation").document("InterviewRoom").update("t3_time",timeUsage)
                }
            }
        }
    }
}