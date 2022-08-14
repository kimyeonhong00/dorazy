package com.example.dorazy

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.dorazy.databinding.ActivityStudyroomBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.concurrent.thread
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class StudyroomActivity : AppCompatActivity() {
    private lateinit var binding:ActivityStudyroomBinding
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth : FirebaseAuth? = null

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

        binding.reservBtn.isEnabled=false
        var table1 = 0
        var table2 = 0
        var table3 = 0
        var table4 = 0
        var t1book : MutableList<String> = mutableListOf("","","","")
        var t2book : MutableList<String> = mutableListOf("","","","")
        var t3book : MutableList<String> = mutableListOf("","","","")
        var t4book : MutableList<String> = mutableListOf("","","","")
        var t1time = mutableListOf("","","","")
        var t2time = mutableListOf("","","","")
        var t3time = mutableListOf("","","","")
        var t4time = mutableListOf("","","","")
        var reservedTable = 0
        val cur = LocalDateTime.now()
        val calendar = Calendar.getInstance()
        val week = calendar.get(Calendar.DAY_OF_WEEK).toChar()
        val formatter = DateTimeFormatter.ofPattern("HHmm")
        val formatted = cur.format(formatter)
        var ind1 = 0
        var ind2 = arrayListOf<Int>(0,0,0,0)

        // 자리 반납 기능
        fun reservCancelClickYes() {
            isReserv = false
            when (auth!!.uid.toString()) {
                t1book[ind1] -> {
                    t1book[ind1] = ""; t1time[ind1] = ""
                    db.collection("reservation").document("StudyRoom").update("table1",--table1)
                    db.collection("reservation").document("StudyRoom").update("t1_booker",t1book)
                    db.collection("reservation").document("StudyRoom").update("t1_time",t1time)
                }
                t2book[ind1] -> {
                    t2book[ind1] = ""; t2time[ind1] = ""
                    db.collection("reservation").document("StudyRoom").update("table2",--table2)
                    db.collection("reservation").document("StudyRoom").update("t2_booker",t2book)
                    db.collection("reservation").document("StudyRoom").update("t2_time",t2time)
                }
                t3book[ind1] -> {
                    t3book[ind1] = ""; t3time[ind1] = ""
                    db.collection("reservation").document("StudyRoom").update("table3",--table3)
                    db.collection("reservation").document("StudyRoom").update("t3_booker",t3book)
                    db.collection("reservation").document("StudyRoom").update("t3_time",t3time)
                }
                else -> {
                    t4book[ind1] = ""; t4time[ind1] = ""
                    db.collection("reservation").document("StudyRoom").update("table4",--table4)
                    db.collection("reservation").document("StudyRoom").update("t4_booker",t4book)
                    db.collection("reservation").document("StudyRoom").update("t4_time",t4time)
                }
            }
            startActivity(studyroomIntent)
        }

        // 현재 자리 데이터 불러오기
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
            var deadLine = ""
            for ( i in 0 until 4) {
                if (t1book[i] == auth!!.uid.toString() || t2book[i] == auth!!.uid.toString() || t3book[i] == auth!!.uid.toString() || t4book[i] == auth!!.uid.toString()) {
                    // 예약했을 경우
                    isReserv = true
                    ind1 = i
                    reservedTable = if (t1book[i] == auth!!.uid.toString()) {
                        1
                    } else if (t2book[i] == auth!!.uid.toString()) {
                        2
                    } else if (t3book[i] == auth!!.uid.toString()) {
                        3
                    } else {
                        4
                    }
                    break
                }
                // 시간 지난 사람들 자리 반납 처리
                if (t1book[i] == "") { ind2[0] = i }
                else{
                    deadLine = t1time[i]
                    if ((deadLine[4]<week) or (formatted.toString().toInt()-deadLine.slice(0 until 4).toInt()>200)){
                        Log.i("TAG","데드라인 넘김!!")
                        t1book[i] = ""
                        t1time[i] = ""
                        db.collection("reservation").document("StudyRoom").update("t1_booker",t1book)
                        db.collection("reservation").document("StudyRoom").update("t1_time",t1time)
                        db.collection("reservation").document("StudyRoom").update("table1",--table1)
                    }
                }
                if (t2book[i] == "") { ind2[1] = i }
                else{
                    deadLine = t2time[i]
                    if ((deadLine[4]<week) or (formatted.toString().toInt()-deadLine.slice(0 until 4).toInt()>200)){
                        t2book[i] = ""
                        t2time[i] = ""
                        db.collection("reservation").document("StudyRoom").update("t2_booker",t2book)
                        db.collection("reservation").document("StudyRoom").update("t2_time",t2time)
                        db.collection("reservation").document("StudyRoom").update("table2",--table2)
                    }
                }
                if (t3book[i] == "") { ind2[2] = i }
                else{
                    deadLine = t3time[i]
                    if ((deadLine[4]<week) or (formatted.toString().toInt()-deadLine.slice(0 until 4).toInt()>200)){
                        t3book[i] = ""
                        t3time[i] = ""
                        db.collection("reservation").document("StudyRoom").update("t3_booker",t3book)
                        db.collection("reservation").document("StudyRoom").update("t3_time",t3time)
                        db.collection("reservation").document("StudyRoom").update("table3",--table3)
                    }
                }
                if (t4book[i] == "") { ind2[3] = i }
                else{
                    deadLine = t4time[i]
                    if ((deadLine[4]<week) or (formatted.toString().toInt()-deadLine.slice(0 until 4).toInt()>200)){
                        t4book[i] = ""
                        t4time[i] = ""
                        db.collection("reservation").document("StudyRoom").update("t4_booker",t4book)
                        db.collection("reservation").document("StudyRoom").update("t4_time",t4time)
                        db.collection("reservation").document("StudyRoom").update("table4",--table4)
                    }
                }
            }
            if (reservedTable != 0) {
                str = doc["t${reservedTable}_time"].toString()
                removeChars.forEach { str = str.replace(it.toString(), "") }
                val tStr = str.split(",").toMutableList()
                deadLine = tStr[ind1]
                // 본인 자리가 2시간 경과 했을 경우
                if ((deadLine[4]<week) or (formatted.toString().toInt()-deadLine.slice(0 until 4).toInt()>200)){
                    reservCancelClickYes()
                    Toast.makeText(this,"2시간이 지나 자동 반납되었습니다.",Toast.LENGTH_SHORT)
                }
            }
            //값 넘겨주기
            studyroomReservIntent.putExtra("table1",table1)
            studyroomReservIntent.putExtra("table2",table2)
            studyroomReservIntent.putExtra("table3",table3)
            studyroomReservIntent.putExtra("table4",table4)
            studyroomReservIntent.putIntegerArrayListExtra("ind",ind2)
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