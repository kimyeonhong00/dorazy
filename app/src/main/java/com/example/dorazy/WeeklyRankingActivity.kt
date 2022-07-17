package com.example.dorazy

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.dorazy.databinding.WeeklyRankingBinding


class WeeklyRankingActivity :AppCompatActivity() {

    private lateinit var binding: WeeklyRankingBinding
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WeeklyRankingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fun getDocs() {
            val docRef = db.collection("User").document("user00001")
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    binding.testtext.text=document.data.toString()
                    Log.d("heyguy","DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d("heyguy","No such document")
                }
            }.addOnFailureListener { exception ->
                Log.d("heyguy", "get failed with ", exception)
            }
        }
        getDocs()
    }


}