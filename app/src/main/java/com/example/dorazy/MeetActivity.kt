package com.example.dorazy

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.dorazy.databinding.ActivityMeetBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.thread


class MeetActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMeetBinding
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth : FirebaseAuth? = null
    private var reservedTable = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        // 인텐트
        val meetIntent = Intent(this, MeetActivity::class.java)
        val mainIntent = Intent(this, MainActivity::class.java)
        val meetReservIntent = Intent(this, MeetReservActivity::class.java)
        val selfStudyIntent = Intent(this, SelfstudyActivity::class.java)
        val studyroomIntent = Intent(this, StudyroomActivity::class.java)

        // 예약 여부
        var isReserv = false
        val groupId = intent.getStringExtra("groupId")


        // 현재 자리 데이터 불러오기
        binding.reservBtn.isEnabled = false
        binding.reservBtn.text = getString(R.string.wait)
        binding.reservBtn.setBackgroundColor(Color.parseColor("#808080"))
        var table1 = false
        var table2 = false
        var table3 = false
        var t1book: String? = null
        var t2book: String? = null
        var t3book: String? = null
        var reservId = groupId ?: auth!!.uid.toString()
        meetReservIntent.putExtra("reservId",reservId)
        var reservTime = ""
        val cur = LocalDateTime.now()
        val calendar = Calendar.getInstance()
        val week = when (calendar.get(Calendar.DAY_OF_WEEK)){
            1 -> 6
            2 -> 0
            3 -> 1
            4 -> 2
            5 -> 3
            6 -> 4
            else -> 5
        }.toString()
        val formatter = DateTimeFormatter.ofPattern("HHmm")
        val formatted = cur.format(formatter)

        // 내 아이디 + 내가 속한 그룹아이디 배열 생성
        val myIds = arrayListOf(auth!!.uid.toString())
        db.collection("groups").whereGreaterThanOrEqualTo("users.${auth!!.uid}", -1).get().addOnSuccessListener {
            for (id in it.toMutableList() ){
                myIds.add(id.id)
            }
        }

        // 자리 반납 기능
        fun reservCancelClickYes() {
            isReserv = false
            when (reservId) {
                t1book -> {
                    table1 = false; t1book = null
                    db.collection("reservation").document("InterviewRoom").update("table1", table1)
                    db.collection("reservation").document("InterviewRoom")
                        .update("t1_booker", t1book)
                    db.collection("reservation").document("InterviewRoom").update("t1_time", "")
                }
                t2book -> {
                    table2 = false; t2book = null
                    db.collection("reservation").document("InterviewRoom").update("table2", table2)
                    db.collection("reservation").document("InterviewRoom")
                        .update("t2_booker", t2book)
                    db.collection("reservation").document("InterviewRoom").update("t2_time", "")
                }
                else -> {
                    table3 = false; t3book = null
                    db.collection("reservation").document("InterviewRoom").update("table3", table3)
                    db.collection("reservation").document("InterviewRoom")
                        .update("t3_booker", t3book)
                    db.collection("reservation").document("InterviewRoom").update("t3_time", "")
                }
            }
            meetIntent.putExtra("isReserv", isReserv)
            meetIntent.putExtra("table1", table1)
            meetIntent.putExtra("table2", table2)
            meetIntent.putExtra("table3", table3)
            startActivity(meetIntent)
        }

        db.collection("reservation").document("InterviewRoom").get().addOnSuccessListener {
            table1 = it["table1"].toString().toBoolean()
            table2 = it["table2"].toString().toBoolean()
            table3 = it["table3"].toString().toBoolean()
            t1book = it["t1_booker"].toString()
            t2book = it["t2_booker"].toString()
            t3book = it["t3_booker"].toString()
            meetReservIntent.putExtra("table1", table1)
            meetReservIntent.putExtra("table2", table2)
            meetReservIntent.putExtra("table3", table3)
            meetReservIntent.putExtra("t1book", t1book)
            meetReservIntent.putExtra("t2book", t2book)
            meetReservIntent.putExtra("t3book", t3book)
            if (myIds.contains(t1book) || myIds.contains(t2book) || myIds.contains(t3book)) {
                isReserv = true
                for (id in myIds) {
                    reservId = id
                    reservTime = if (t1book == reservId) {
                        reservedTable = 1
                        it["t1_time"].toString()
                    } else if (t2book == reservId) {
                        reservedTable = 2
                        it["t2_time"].toString()
                    } else {
                        reservedTable = 3
                        it["t3_time"].toString()
                    }
                }
            }
            if ((isReserv) and (reservTime!="")) {
                if ((reservTime[4]<week[0]) or (formatted.toString().toInt()-reservTime.slice(0 until 4).toInt()>200)){
                    reservCancelClickYes()
                }
            }
        }


        //예약 기능을 반영한 테이블 img
        thread(start = true) {
            Thread.sleep(1700)
            binding.reservBtn.setBackgroundColor(Color.parseColor("#002244"))
            binding.reservBtn.text = getString(R.string.reserve)
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
                binding.reservBtn.text = getString(R.string.return_seat)
            }
            runOnUiThread {
                if (!isReserv) {
                    if (groupId == null) {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("예약자 확인")
                        builder.setMessage("1인 예약을 진행하시겠습니까?")

                        val listener = DialogInterface.OnClickListener { _, p1 ->
                            when (p1) {
                                DialogInterface.BUTTON_POSITIVE -> {}
                                DialogInterface.BUTTON_NEGATIVE ->
                                    startActivity(
                                        Intent(
                                            this,
                                            GroupActivity::class.java
                                        ).putExtra("call", 3)
                                    )
                            }
                        }

                        builder.setPositiveButton("1인 예약하기", listener)
                        builder.setNegativeButton("그룹으로 예약하기", listener)
                        builder.show()
                    }
                }
                binding.reservBtn.isEnabled = true
            }
        }


        // 자리 반납
        fun showDialog() {
            val builder2 = AlertDialog.Builder(this)
            builder2.setTitle("자리 반납")
            builder2.setMessage("자리를 반납하시겠습니까?")

            val listener = DialogInterface.OnClickListener { _, p1 ->
                when (p1) {
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
            startActivity(mainIntent)
            finish()
        }

        // 예약 or 반납 버튼
        binding.reservBtn.setOnClickListener {
            if (!isReserv) {
                startActivity(meetReservIntent)
            } else {
                showDialog()
            }

        }
        // 예약 기능 추가
    }

}