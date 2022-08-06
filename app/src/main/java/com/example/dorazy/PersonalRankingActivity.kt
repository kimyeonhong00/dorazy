package com.example.dorazy

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.dorazy.databinding.PersonalRankingBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import kotlin.math.roundToInt


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

        val dataArray = ArrayList<Record>()
        var personalRecord: Record

        database.collection("User").get().addOnSuccessListener { doc ->
            for (d in doc){
                if (d.id== cu!!.uid) {
                    binding.MyTime.text = d.data["studyTime"].toString()
                    binding.MyName.text = d.data["name"].toString()
                }
                personalRecord = Record(d.data["name"].toString(),d.data["studyTime"].toString().toInt())
                dataArray.add(personalRecord)
            }
        }

        thread (start = true){
            Thread.sleep(1000)
            dataArray.sort()
            runOnUiThread {
                for (i in 0 until dataArray.size){
                    if (dataArray[i].name==binding.MyName.text){
                        binding.MyTime.text = dataToTime(dataArray[i].time)
                        binding.MyRanking.text = "${dataArray.size-i}위"
                    }
                    val personalRankingBarView = LayoutInflater.from(this).inflate(R.layout.ranking_personal_bar_layout,null,false)
                    personalRankingBarView.findViewById<TextView>(R.id.rank).text="${dataArray.size-i}위"
                    personalRankingBarView.findViewById<TextView>(R.id.rankname).text= nameEncode(dataArray[i].name)
                    personalRankingBarView.findViewById<TextView>(R.id.ranktime).text= dataToTime(dataArray[i].time)
                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.setMargins(0,0,0,(5 * resources.displayMetrics.density).roundToInt())
                    personalRankingBarView.layoutParams = layoutParams
                    binding.RankingList.addView(personalRankingBarView,0)
                }
            }
        }

        binding.PersonalRankingBackButton.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun dataToTime(time: Int):String{
        val sec: Int = time%60
        val min: Int = time%3600/60
        val hour: Int = time/3600
        return "$hour:$min:$sec"
    }

    private fun nameEncode(name:String):String{
        var result = ""
        for (i in name.indices){
            if (i%2==1){
                result += "*"
            }else{
                result += name[i]
            }
        }
        return result
    }
}