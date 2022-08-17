package com.example.dorazy

import android.os.Build
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate

class sharePage : AppCompatActivity(){
    private lateinit var progressBar: ProgressBar
    private lateinit var dateview: TextView
    @RequiresApi(Build.VERSION_CODES.O)
    val onlyDate: LocalDate = LocalDate.now()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sharepage)
        dateview = findViewById(R.id.textView19)
        progressBar = findViewById(R.id.progress_horizontal)
        var t = 0
        var g = 1
        t = intent?.getStringExtra("studyTime")?.toInt()!!
        g = intent?.getStringExtra("goalTime")?.toInt()!!
        println(g)
        println(t)
        println("결과창")

        val progress = t*100 / (g *3600)
        dateview.text = onlyDate.toString()

        progressBar.setProgress(progress)

    }
}