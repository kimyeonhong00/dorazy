package com.example.dorazy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dorazy.databinding.MyRankingStatsBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import android.util.Log
import kotlinx.coroutines.flow.callbackFlow
import kotlin.concurrent.thread


const val TAG : String = "?!"


class RankingStatsActivity : AppCompatActivity(){

    private lateinit var binding: MyRankingStatsBinding
    private lateinit var database: FirebaseFirestore
    private val cu = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MyRankingStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.firestore
        var groupRank = ArrayList<Float>()
        var totalRank = ArrayList<Float>()
        var sTime = ArrayList<Float>()
        var labels = ArrayList<String>()

        val db = database.collection("User").document(cu!!.uid).collection("record").get().addOnSuccessListener { col ->
            for (doc in col){
                labels += doc.id
                groupRank += doc["groupRanking"].toString().toFloat()
                totalRank += doc["totalRanking"].toString().toFloat()
                sTime += doc["studyTime"].toString().toFloat()
            }
        }

        //그 이후 처리
        thread(start = true) {
            Thread.sleep(1000)
            Log.i(TAG, labels.toString())
            val lineChart = binding.LineChart
            val entries1 = ArrayList<Entry>()
            val entries2 = ArrayList<Entry>()
            val entries3 = ArrayList<Entry>()

            for (i in 0 until groupRank.size) {
                Log.i(TAG, i.toString())
                entries1.add(Entry(i.toFloat(), groupRank[i]))
                entries2.add(Entry(i.toFloat(), totalRank[i]))
                entries3.add(Entry(i.toFloat(), sTime[i]))
            }

            val dataSet1 = LineDataSet(entries1, "")
            val dataSet2 = LineDataSet(entries2, "")
            val dataSet3 = LineDataSet(entries3, "")
            lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            lineChart.getTransformer(YAxis.AxisDependency.LEFT)
            lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            val data = LineData()
            data.addDataSet(dataSet1)
            data.addDataSet(dataSet2)
            data.addDataSet(dataSet3)
            lineChart.data = data
            lineChart.invalidate()
        }
        binding.MyRankingStatsBackButton.setOnClickListener{
            super.onBackPressed()
        }
    }
}