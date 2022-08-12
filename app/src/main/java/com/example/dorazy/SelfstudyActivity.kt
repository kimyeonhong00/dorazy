package com.example.dorazy

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.dorazy.databinding.ActivitySelfstudyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlin.concurrent.thread


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
        val studyroomIntent = Intent(this, StudyroomActivity::class.java)

        // 현재 예약 데이터 불러오기
        binding.reservBtn.isEnabled=false
        val reservStatus = ArrayList<List<String>>()
        val groupId = intent.getStringExtra("groupId")
        selfstudyReservIntent.putExtra("groupId",groupId)
        var booked = false
        db.collection("reservation").document("SelfStudySpace").get().addOnSuccessListener {doc ->
            val removeChars = "[] "
            var str = doc["Mon"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            reservStatus.add(str.split(","))
            str = doc["Tue"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            reservStatus.add(str.split(","))
            str = doc["Wed"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            reservStatus.add(str.split(","))
            str = doc["Thur"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            reservStatus.add(str.split(","))
            str = doc["Fri"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            reservStatus.add(str.split(","))
            str = doc["Sat"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            reservStatus.add(str.split(","))
        }

        thread {
            Thread.sleep(1000)
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
                                timetableTextview.setBackgroundColor(Color.parseColor("#002244"))
                                booked = true
                            }
                        }
                    }
                }
            }
            runOnUiThread {
                // 예약 안했으면 버튼 enable
                if (!booked) {
                    binding.reservBtn.isEnabled = true
                }
            }
        }


        // 미팅룸 activity로 전환
        binding.meetChangeBtn.setOnClickListener {
            startActivity(meetIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }

        // 스터디룸 activity로  전환
        binding.studyroomBtn.setOnClickListener {
            startActivity(studyroomIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }

        // 메인으로 돌아가기 activity
        binding.backBtn.setOnClickListener {
            startActivity(mainIntent)
            finish()
        }

        //예약 버튼
        binding.reservBtn.setOnClickListener {
            startActivity(selfstudyReservIntent)
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
}