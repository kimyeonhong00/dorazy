package com.example.dorazy

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.dorazy.databinding.PersonalRankingBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread


class Record (var name: String, var time: Int) :Comparable<Record>{
    override fun compareTo(other: Record): Int {
        return if (other.time<time){
            1
        } else {
            -1
        }
    }
}

class PersonalRankingActivity :AppCompatActivity() {

    private lateinit var binding: PersonalRankingBinding
    private lateinit var database: FirebaseFirestore
    private val cu = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PersonalRankingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = Firebase.firestore

        var dataArray = ArrayList<Record>()
        var personalRecord = Record("",0)

        val db = database.collection("User").get().addOnSuccessListener { doc ->
            for (d in doc){
                if (d.id.toString()== cu!!.uid) {
                    binding.MyTime.text = d.data["studyTime"].toString()
                    binding.MyName.text = d.data["name"].toString()
                }
                personalRecord = Record(d.data["name"].toString(),d.data["studyTime"].toString().toInt())
                dataArray.add(personalRecord)
            }
        }

        thread (start = true){
            Thread.sleep(1000)
            dataArray.sortDescending()
            runOnUiThread {
                for (i in 0 until dataArray.size){
                    val personalRankingBarView = LayoutInflater.from(this).inflate(R.layout.ranking_personal_bar_layout,binding.RankingList)
                }
            }
            Log.i("BB", dataArray.toString())

        }
        // 화면 텍스트 변경

        binding.PersonalRankingBackButton.setOnClickListener {
            super.onBackPressed()
        }
    }

    fun dataToTime(time: Int):String{
        var sec = 0
        var min = 0
        var hour = 0
        sec = time%60
        min = time%3600/60
        hour = time/3600
        return "$hour:$min:$sec"
    }
}