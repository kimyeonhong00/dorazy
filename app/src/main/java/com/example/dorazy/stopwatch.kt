package com.example.dorazy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.timerTask
import android.os.Bundle
import java.util.*
import androidx.appcompat.widget.AppCompatImageButton
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Gravity
import android.widget.*
import android.widget.ProgressBar
import kotlin.concurrent.thread
import com.google.firebase.firestore.*
import com.google.firebase.storage.*


class stopwatch : AppCompatActivity() {
    private var time = 0
    private var isRunning = false
    private var first = true
    private var timerTask: Timer? = null
    private var index :Int = 1
    private lateinit var txtTime: TextView
    private lateinit var goalTime: TextView
    private lateinit var resultText: TextView

    private lateinit var progressBar: ProgressBar

    private lateinit var startBtn: AppCompatImageButton
    private lateinit var groupBtn: AppCompatImageButton
    private lateinit var finishBtn: AppCompatImageButton
    private lateinit var backBtn: AppCompatImageButton
    var goal_list : Array<String> = arrayOf("1시간","2시간","3시간","4시간","5시간","6시간","7시간","8시간","9시간","10시간")
    var goal = 0
    var backKeyPressedTime : Long = 0
    var ing = 0
    var firestore: FirebaseFirestore? = null
    var currentuser = UserModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stopwatch)

        //View inflate
        txtTime = findViewById(R.id.txtText)
        goalTime = findViewById(R.id.goalText)
        startBtn = findViewById(R.id.startBtn)
        groupBtn = findViewById(R.id.groupBtn)
        finishBtn = findViewById(R.id.finishBtn)
        backBtn = findViewById(R.id.backtBtn)
        progressBar = findViewById(R.id.progress_horizontal)
        //버튼 클릭 리스너
        startBtn.setOnClickListener{
            if(first){
                // 처음 시작이면 목표 시간 input 받기 - 다이얼로그 리스트
                var builder = AlertDialog.Builder(this)
                var listener = DialogInterface.OnClickListener{
                        dialog, which -> goal = which+1
                    //goalTime.text = goal_list[which]
                }
                builder.setTitle("목표시간 설정").setItems(goal_list, listener)
                builder.show()
                first = false
                runOnUiThread {
                    goalTime.text = "$goal : 00 : 00"
                }
            }
            else {
                isRunning = !isRunning
                if (isRunning) {
                    start()

                } else pause()
            }
        }
        groupBtn.setOnClickListener {
            //그룹 화면으로 전환
            startActivity(Intent(this, grouppage::class.java))
        }
        finishBtn.setOnClickListener {
            //끝나면 사용자 시간 set
            firestore?.collection("Users")?.document(currentuser.uid)?.set(time!!)
            //끝 버튼 누르면 공유 페이지로 이동
            if(time!=0) {//공유창으로 이동
                startActivity(Intent(this, sharePage::class.java))
            }
            reset() // 타이머 리셋
            first = true // first 변수 true로 변경하여 처음 시작처럼 동작

        }
        backBtn.setOnClickListener {
            //뒤로가기 버튼 클릭
            onBackPressed()
        }

    }
    //뒤로 가기 버튼 클릭하면 메인 페이지로 이동
    override fun onBackPressed() {
        if(isRunning == false) {//홈화면으로 이동
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        else{ //측정중엔 뒤로 가기 작동 x 안내창 띄우기
            val toast = Toast.makeText(this@stopwatch, "측정중엔 화면을 벗어날 수 없어요!", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER,0,0)
            toast.show()
        }
    }
    //타이머 시작
    private fun start() {
        //일시정지 버튼으로 변경
        startBtn.setImageResource(R.drawable.ic_pause)
        //타이머 선언
        timerTask = kotlin.concurrent.timer(period = 1000) { //반복주기는 peroid 프로퍼티로 설정, 단위는 1000분의 1초 (period = 1000, 1초)
            time++ // 1초에 1씩 증가
            val sec = time %60 //초단위
            val min = time %3600 /60 //분단위
            val hour = time /3600 //시간단위
            val progress = (time*100)/(goal*3600)

            // UI조작을 위한 메서드
            runOnUiThread {
                txtTime.text = "$hour : $min : $sec"
                progressBar.setProgress(progress)

            }
        }
    }
    //타이머 일시정지
    private fun pause() {
        timerTask?.cancel();
    }
    //타이머 리셋
    private fun reset() {
        timerTask?.cancel() // timerTask가 null이 아니라면 cancel() 호출

        time = 0 // 시간저장 변수 초기화
        isRunning = false // 현재 진행중인지 판별하기 위한 Boolean변수 false 세팅
        txtTime.text = "00 : 00 : 00"
        index = 1
    }


}