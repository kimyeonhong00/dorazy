package com.example.dorazy

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.dorazy.databinding.ActivityStudyroomBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.concurrent.thread
import java.time.LocalDateTime


class StudyroomActivity : AppCompatActivity() {
    private lateinit var binding:ActivityStudyroomBinding
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth : FirebaseAuth? = null
    private var reservedTable = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyroomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        // 예약 여부
        var isReserv = intent.getBooleanExtra("isReserv", false)

        // 인텐트
        val meetIntent = Intent(this, MeetActivity::class.java)
        val mainIntent = Intent(this, MainActivity::class.java)
        val studyroomReservIntent = Intent(this, StudyroomReservActivity::class.java)
        val selfStudyIntent = Intent(this, SelfstudyActivity::class.java)
        val studyroomIntent = Intent(this, StudyroomActivity::class.java)

        // 현재 자리 데이터 불러오기
        binding.reservBtn.isEnabled=false
        var table1 = 0
        var table2 = 0
        var table3 = 0
        var table4 = 0
        var t1book : MutableList<String> = mutableListOf("","","","")
        var t2book : MutableList<String> = mutableListOf("","","","")
        var t3book : MutableList<String> = mutableListOf("","","","")
        var t4book : MutableList<String> = mutableListOf("","","","")
        var ind = 0
        var t =
        db.collection("reservation").document("StudyRoom").get().addOnSuccessListener { doc ->
            table1 = doc["table1"].toString().toInt()
            table2 = doc["table2"].toString().toInt()
            table3 = doc["table3"].toString().toInt()
            table4 = doc["table4"].toString().toInt()
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
            studyroomReservIntent.putExtra("table1",table1)
            studyroomReservIntent.putExtra("table2",table2)
            studyroomReservIntent.putExtra("table3",table3)
            studyroomReservIntent.putExtra("table4",table4)
            for ( i in 0 until 4) {
                if (t1book[i] == auth!!.uid.toString() || t2book[i] == auth!!.uid.toString() || t3book[i] == auth!!.uid.toString() || t4book[i] == auth!!.uid.toString()) {
                    isReserv = true
                    ind = i
                    reservedTable = if (t1book[i] == auth!!.uid.toString()) {
                        1
                    } else if (t2book[i] == auth!!.uid.toString()) {
                        2
                    } else if (t3book[i] == auth!!.uid.toString()) {
                        3
                    } else if (t4book[i] == auth!!.uid.toString()) {
                        4
                    } else{
                        0
                    }
                    break
                }
            }
            var t = if (reservedTable != 0) {
                str = doc["t${reservedTable}_time"].toString()
                removeChars.forEach { str = str.replace(it.toString(), "") }
                val tStr = str.split(",").toMutableList()
                tStr[ind].toInt()
            } else {
                0
            }
            studyroomReservIntent.putExtra("ind",ind)
        }


        // 자리 반납 기능
        fun reservCancelClickYes() {
            isReserv = false
            when (auth!!.uid.toString()) {
                t1book[ind] -> {
                    table1--; t1book[ind] = ""
                    db.collection("reservation").document("StudyRoom").update("table1",table1)
                    db.collection("reservation").document("StudyRoom").update("t1_booker",t1book)
                    db.collection("reservation").document("StudyRoom").update("t1_time",0)
                }
                t2book[ind] -> {
                    table2--; t2book[ind] = ""
                    db.collection("reservation").document("StudyRoom").update("table2",table2)
                    db.collection("reservation").document("StudyRoom").update("t2_booker",t2book)
                    db.collection("reservation").document("StudyRoom").update("t2_time",0)
                }
                t3book[ind] -> {
                    table3--; t3book[ind] = ""
                    db.collection("reservation").document("StudyRoom").update("table3",table3)
                    db.collection("reservation").document("StudyRoom").update("t3_booker",t3book)
                    db.collection("reservation").document("StudyRoom").update("t3_time",0)
                }
                else -> {
                    table4--; t4book[ind] = ""
                    db.collection("reservation").document("StudyRoom").update("table4",table4)
                    db.collection("reservation").document("StudyRoom").update("t4_booker",t4book)
                    db.collection("reservation").document("StudyRoom").update("t4_time",0)
                }
            }
            startActivity(studyroomIntent)
        }

        //예약 기능을 반영한 테이블 img
        thread (start = true){
            Thread.sleep(1000)
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

            if (isReserv) {
                // 2시간 타이머
                binding.reservBtn.text = getString(R.string.return_seat)
            }
            runOnUiThread {
                binding.t1Text.text = table1.toString()+getString(R.string.now_using)
                binding.t2Text.text = table2.toString()+getString(R.string.now_using)
                binding.t3Text.text = table3.toString()+getString(R.string.now_using)
                binding.t4Text.text = table4.toString()+getString(R.string.now_using)
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

        binding.meetChangeBtn.setOnClickListener {
            meetIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(meetIntent)
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
                studyroomReservIntent.putExtra("isReserv", isReserv)
                startActivity(studyroomReservIntent)
            }
            else
            {
                showDialog()
            }

        }
    }

}