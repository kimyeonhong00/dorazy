package com.example.dorazy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.timerTask
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.util.*
//import kotlinx.android.synthetic.main.activity_main.*


class stopwatch : AppCompatActivity() {
    private var time = 0
    private var isRunning = false
    private var timerTask: Timer? = null
    private var index :Int = 1
    private lateinit var txtTime: TextView
    private lateinit var goalTime: TextView

    private lateinit var startBtn: Button
    private lateinit var groupBtn: Button
    private lateinit var finishBtn: Button
    private lateinit var backBtn: Button
    private lateinit var lap_Layout: LinearLayout

    var backKeyPressedTime : Long = 0


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
        lap_Layout = findViewById(R.id.lap_Layout)

        //버튼 클릭 리스너
        startBtn.setOnClickListener {
            isRunning = !isRunning
            if (isRunning) start() else pause()
        }
        groupBtn.setOnClickListener {
            //그룹 화면으로 전환 (.xml)
            //startActivity(Intent(this@stopwatch, **group page**::class.java))
            reset()
        }
        finishBtn.setOnClickListener {
            if(time!=0) {//화면 공유창으로 이동
            // lapTime()
            }
        }
        backBtn.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
    private fun start() {
        //startBtn.setImageResource //setImageResource(R.drawable.ic_pause)
        //startBtn.text ="멈추기"
        timerTask = kotlin.concurrent.timer(period = 1000) { //반복주기는 peroid 프로퍼티로 설정, 단위는 1000분의 1초 (period = 1000, 1초)
            time++ // 1초에 1씩 증가
            val sec = time %60 //초단위로 증가
            val min = time %3600 /60
            val hour = time /3600

            // UI조작을 위한 메서드
            runOnUiThread {
                txtTime.text = "{$hour} : {$min} : {$sec}"

            }
        }
    }

    private fun pause() {

        timerTask?.cancel();
    }

    private fun reset() {
        timerTask?.cancel() // timerTask가 null이 아니라면 cancel() 호출

        time = 0 // 시간저장 변수 초기화
        isRunning = false // 현재 진행중인지 판별하기 위한 Boolean변수 false 세팅
        txtTime.text = "00 : 00 : 00"
        startBtn.text ="시작"
        lap_Layout.removeAllViews() // Layout에 추가한 기록View 모두 삭제
        index = 1
    }

    /*private fun lapTime() {
        val lapTime = time // 함수 호출 시 시간(time) 저장

        // apply() 스코프 함수로, TextView를 생성과 동시에 초기화
        val textView = TextView(this).apply {
            setTextSize(20f) // fontSize 20 설정
        }
        textView.text = "${lapTime / 100}.${lapTime % 100}" // 출력할 시간 설정

        lap_Layout.addView(textView,0) // layout에 추가, (View, index) 추가할 위치(0 최상단 의미)
        index++ // 추가된 View의 개수를 저장하는 index 변수
    }*/


}