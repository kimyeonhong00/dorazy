package com.example.dorazy

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.dorazy.databinding.CreateProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

private var auth: FirebaseAuth? = null

class CreateProfileActivity :AppCompatActivity() {

    private lateinit var binding:CreateProfileBinding
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.majorSpinner2.adapter = ArrayAdapter.createFromResource(this,R.array.major,R.layout.spinner_item)
        val majorArray = resources.getStringArray(R.array.major)

        binding.startButton.setOnClickListener{
            if (binding.nameInput2.text!=null){
                val newData = hashMapOf(
                    "name" to binding.nameInput2.text.toString(),
                    "major" to majorArray[binding.majorSpinner2.selectedItemPosition],
                    "user_id" to auth!!.currentUser!!.email.toString(),
                    "root" to 0,
                    "studyTime" to 0
                )
                db.collection("User").document(auth!!.uid.toString()).set(newData)
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
        }
    }

}