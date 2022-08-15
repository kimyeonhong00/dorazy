package com.example.dorazy

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.example.dorazy.databinding.ActivitySelfstudyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlin.concurrent.thread
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class SelfstudyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelfstudyBinding
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelfstudyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        // 인텐트
        val meetIntent = Intent(this, MeetActivity::class.java)
        val mainIntent = Intent(this, MainActivity::class.java)
        val selfstudyReservIntent = Intent(this, SelfstudyReservActivity::class.java)
        val selfstudyNextweekIntent = Intent(this, SelfstudyNextweekActivity::class.java)
        val selfstudyIntent = Intent(this, SelfstudyActivity::class.java)
        val studyroomIntent = Intent(this, StudyroomActivity::class.java)

        // 현재 예약 데이터 불러오기
        binding.reservBtn.isEnabled=false
        val reservStatus = ArrayList<List<String>>()
        val groupId = intent.getStringExtra("groupId")
        selfstudyReservIntent.putExtra("groupId",groupId)
        selfstudyNextweekIntent.putExtra("groupId",groupId)
        meetIntent.putExtra("groupId",groupId)

        val curTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HHmm")
        val cur = curTime.format(formatter)
        val calendar = Calendar.getInstance()
        val week = toWeekInt(calendar.get(Calendar.DAY_OF_WEEK))
        var isReserved = false
        var past = false
        var hh: String
        var mm: String
        var myReserve = ArrayList<Array<Int>>()
        db.collection("reservation").document("SelfStudySpace").get().addOnSuccessListener {doc ->
            val removeChars = "[] "
            var str = doc["mon"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            reservStatus.add(str.split(","))
            str = doc["tue"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            reservStatus.add(str.split(","))
            str = doc["wed"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            reservStatus.add(str.split(","))
            str = doc["thur"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            reservStatus.add(str.split(","))
            str = doc["fri"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            reservStatus.add(str.split(","))
            str = doc["sat"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            reservStatus.add(str.split(","))
        }

        thread {
            Thread.sleep(1500)
            // 표 처음 상태 색칠
            for (i in 0 until 6){
                for (j in 0 until 22){
                    if(reservStatus[i].isEmpty()){
                        continue
                    }
                    val reservTeams = reservStatus[i][j].split("/").toMutableList()
                    val drId = resources.getIdentifier(toWeekString(i)+(j+1),"id",applicationContext.packageName)
                    val timetableTextview = findViewById<TextView>(drId)
                    runOnUiThread {
                        if(reservTeams[0]==""){
                            reservTeams.clear()
                        }
                        when (reservTeams.size) {
                            1 -> timetableTextview.setBackgroundColor(Color.parseColor("#EDD74C"))
                            2 -> timetableTextview.setBackgroundColor(Color.parseColor("#E09B53"))
                            3 -> timetableTextview.setBackgroundColor(Color.parseColor("#F04C43"))
                        }
                        for (t in reservTeams) {
                            if (groupId == t) {
                                hh = (9+j/2).toString()
                                mm = if ((j%2)==1){
                                    "30"
                                } else{
                                    "00"
                                }
                                val hhmm = (hh+mm).toInt()
                                past = (i<week) or ((i==week) and (hhmm<cur.toInt()))
                                myReserve.add(arrayOf(i,j))
                                timetableTextview.setBackgroundColor(Color.parseColor("#002244"))
                                isReserved = true
                                if (!past) {
                                    binding.reservBtn.text = getString(R.string.cancel_reserv)
                                } else {
                                    binding.reservBtn.text = getString(R.string.ban)
                                }
                            }
                        }
                    }
                }
            }
            runOnUiThread {
                binding.reservBtn.isEnabled = true
                if (past){
                    binding.reservBtn.setBackgroundColor(Color.parseColor("#808080"))
                }
            }
        }

        fun cancelReservClickYes() {
            var st = 0
            if (myReserve[0][0] == week){
                for (i in 0 until myReserve.size){
                    hh = (9+myReserve[i][1]/2).toString()
                    mm = if ((myReserve[i][1]%2)==1){
                        "30"
                    } else{
                        "00"
                    }
                    val hhmm = (hh+mm).toInt()
                    if (hhmm>cur.toInt()) {
                        st = i
                        break
                    }
                }
            }
            val size = myReserve.size
            myReserve = ArrayList(myReserve.subList(st,size))
            val newStatus = reservStatus[myReserve[0][0]].toMutableList()
            for (record in myReserve) {
                if (newStatus[record[1]].contains("/")){
                    newStatus[record[1]] = newStatus[record[1]].replace("$groupId/","")
                    newStatus[record[1]] = newStatus[record[1]].replace("/$groupId","")
                } else{
                    newStatus[record[1]] = ""
                }
            }
            db.collection("reservation").document("SelfStudySpace").update(toWeekString(myReserve[0][0]),newStatus)
            startActivity(selfstudyIntent)
        }

        // 자리 반납
        fun showDialog1() {
            val builder2 = AlertDialog.Builder(this)
            hh = (9+myReserve[0][1]/2).toString()
            mm = if ((myReserve[0][1]%2)==1){
                "30"
            } else{
                "00"
            }
            val hhmm = (hh+mm).toInt()
            if ((myReserve[0][0] == week) and (hhmm<cur.toInt())) {  // 이용 중
                builder2.setTitle("이용 종료")
                builder2.setMessage("이용을 종료 하시겠습니까?")
            } else {  // 이용 전
                builder2.setTitle("예약 취소")
                builder2.setMessage("예약을 취소 하시겠습니까?")
            }
            val listener = DialogInterface.OnClickListener { _, p1 ->
                when(p1) {
                    DialogInterface.BUTTON_POSITIVE ->
                        cancelReservClickYes() // 명령어
                    DialogInterface.BUTTON_NEGATIVE ->
                        Toast.makeText(this, "취소하셨습니다", Toast.LENGTH_SHORT).show()
                }
            }

            builder2.setPositiveButton("예", listener)
            builder2.setNegativeButton("아니요", listener)
            builder2.show()
        }

        // 이용 제한 안내
        fun showDialog2() {
            val timeGuide = ConfirmDialog("알림","금주의 예약 가능 횟수를\n초과하였습니다.","확인")
            timeGuide.isCancelable=false
            timeGuide.show(this.supportFragmentManager,"ConfirmDialog")
        }

        // 미팅룸 activity로 전환
        binding.meetChangeBtn.setOnClickListener {
            startActivity(meetIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }

        // 스터디룸 activity로 전환
        binding.studyroomBtn.setOnClickListener {
            startActivity(studyroomIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }

        // 다음주 activity로 전환
        binding.toNext.setOnClickListener {
            startActivity(selfstudyNextweekIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }

        // 메인으로 돌아가기 activity
        binding.backBtn.setOnClickListener {
            startActivity(mainIntent)
            finish()
        }

        //예약 버튼
        binding.reservBtn.setOnClickListener {
            if (groupId == null) {  //그룹 아이디 없으면 그룹페이지로 이동
                val builder: androidx.appcompat.app.AlertDialog.Builder =
                    androidx.appcompat.app.AlertDialog.Builder(this)
                builder.setTitle("안내")
                builder.setMessage("자율학습공간은 그룹예약만 가능합니다. 그룹페이지로 이동할까요?")
                val listener = DialogInterface.OnClickListener { _, p1 ->
                    when (p1) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            startActivity(Intent(this, GroupActivity::class.java).putExtra("call",0))
                            finish()
                        }
                        DialogInterface.BUTTON_NEGATIVE ->
                            Toast.makeText(this, "취소하셨습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                builder.setPositiveButton("이동하기", listener)
                builder.setNegativeButton("취소", listener)
                builder.show()
            } else if (!isReserved) {
                startActivity(selfstudyReservIntent)
            } else if (!past) {
                showDialog1()
            } else{
                showDialog2()
            }
        }

    }

    //숫자를 요일로
    private fun toWeekString(n : Int) :String{
        return when (n){
            0-> "mon"
            1-> "tue"
            2-> "wed"
            3-> "thur"
            4-> "fri"
            else -> "sat"
        }
    }

    private fun toWeekInt(n:Int):Int{
        return when (n){
            1 -> 6
            2 -> 0
            3 -> 1
            4 -> 2
            5 -> 3
            6 -> 4
            else -> 5
        }
    }
}