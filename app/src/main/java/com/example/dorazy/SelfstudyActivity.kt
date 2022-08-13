package com.example.dorazy

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
        val selfstudyIntent = Intent(this, SelfstudyActivity::class.java)
        val studyroomIntent = Intent(this, StudyroomActivity::class.java)

        // 현재 예약 데이터 불러오기
        binding.reservBtn.isEnabled=false
        val reservStatus = ArrayList<List<String>>()
//        val groupId = intent.getStringExtra("groupId")
        val groupId = "Lim" //수정할것@@@@
        selfstudyReservIntent.putExtra("groupId",groupId)
        var isReserved = false
        val myReserve = ArrayList<Array<Int>>()
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
                                myReserve.add(arrayOf(i,j))
                                timetableTextview.setBackgroundColor(Color.parseColor("#002244"))
                                isReserved = true
                                binding.reservBtn.text = getString(R.string.cancel_reserv)
                            }
                        }
                    }
                }
            }
            runOnUiThread {
                binding.reservBtn.isEnabled = true
            }
        }

        fun cancelReservClickYes(cancelType:Int) {
            if (cancelType==0) {
                val newStatus = reservStatus[myReserve[0][0]].toMutableList()
                for (record in myReserve) {
                    if (newStatus[record[1]].contains("/")){
                        newStatus[record[1]].replace("$groupId/","")
                        newStatus[record[1]].replace("/$groupId","")
                    } else{
                        newStatus[record[1]] = ""
                    }
                }
                db.collection("reservation").document("SelfStudySpace").update(toWeekString(myReserve[0][0]),newStatus)
            } else {

            }
            startActivity(selfstudyIntent)
        }

        // 자리 반납
        fun showDialog() {
            val builder2 = AlertDialog.Builder(this)
            var cancelType = 0
            // 이용 전 if () {
            builder2.setTitle("예약 취소")
            builder2.setMessage("예약을 취소 하시겠습니까?")
            // 이용 중 else { cancelType = 1
            // builder2.setTitle("이용 종료")
            // builder2.setMessage("이용을 종료 하시겠습니까?")}
            val listener = DialogInterface.OnClickListener { _, p1 ->
                when(p1) {
                    DialogInterface.BUTTON_POSITIVE ->
                        cancelReservClickYes(cancelType) // 명령어
                    DialogInterface.BUTTON_NEGATIVE ->
                        Toast.makeText(this, "취소하셨습니다", Toast.LENGTH_SHORT).show()
                }
            }

            builder2.setPositiveButton("예", listener)
            builder2.setNegativeButton("아니요", listener)
            builder2.show()
        }

        // 미팅룸 activity로 전환
        binding.meetChangeBtn.setOnClickListener {
            startActivity(meetIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }

        // 스터디룸 activity로 전환
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
            if (!isReserved) {
                startActivity(selfstudyReservIntent)
            } else{
                showDialog()
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
}