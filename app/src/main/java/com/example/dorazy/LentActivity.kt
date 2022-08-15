package com.example.dorazy

import android.app.AlertDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.dorazy.databinding.ActivityLentBinding
import com.google.firebase.firestore.FirebaseFirestore

class LentActivity:AppCompatActivity() {
    private var mcounting: Int ? =null
    private var wcounting: Int ?=null
    private var mlenting: Boolean ?= false
    private var wlenting:Boolean?=false

    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var binding:ActivityLentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val groupID: String? = intent?.getStringExtra("groupID")
        val multiTabRef = firestore.collection("goods").document("multiTab")
            if(multiTabRef !=null){
                multiTabRef.get()
                    .addOnSuccessListener { document ->
                        mcounting = document.data?.get("count")?.toString()?.toInt()
                        binding.mutlitabCount.text = "$mcounting/10"
                    }
            }else{
                Log.d(TAG,"multitab get Error")
            }

        val boardRef = firestore.collection("goods").document("whiteBoard")
            if(boardRef!=null){
                boardRef.get()
                    .addOnSuccessListener { document->
                        wcounting = document.data?.get("count")?.toString()?.toInt()
                        binding.boardCount.text = "$wcounting/10"
                    }
            }else{
                Log.d(TAG,"board get Error")
            }



        binding.mutlitabLent.setOnClickListener {
            val dialog = LentDialog(this)
            var student:String ?=null
            dialog.showDialog()
            dialog.setOnClickListener(object : LentDialog.OnDialogClickListener{
                override fun onClicked(name: String) {
                    student= name
                }
            })
            mcounting = mcounting?.minus(1)
            multiTabRef.update("count",mcounting)
            mlenting = true
        }
        binding.boardLent.setOnClickListener {
            val dialog1 = LentDialog(this)
            var student2:String?=null
            dialog1.showDialog()
            dialog1.setOnClickListener(object : LentDialog.OnDialogClickListener{
                override fun onClicked(name: String) {
                    student2 = name
                }
            })
            wcounting = wcounting?.minus(1)
            boardRef.update("count",wcounting)
            wlenting = true
        }
        binding.returnButton.setOnClickListener {
            if(mlenting == true){
                mlenting =false
                multiTabRef.update("count", mcounting?.plus(1))
            }
            if(wlenting == true){
                wlenting = false
                boardRef.update("count", wcounting?.plus(1))
            }
        }

    }
}