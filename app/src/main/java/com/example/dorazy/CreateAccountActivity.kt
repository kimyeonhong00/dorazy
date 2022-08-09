package com.example.dorazy

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.dorazy.databinding.CreateAccountBinding
import com.google.firebase.firestore.FirebaseFirestore

private var auth: FirebaseAuth? = null

class CreateAccountActivity : AppCompatActivity(){

    private lateinit var binding:CreateAccountBinding
    private val db:FirebaseFirestore = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.majorSpinner.adapter = ArrayAdapter.createFromResource(this,R.array.major,R.layout.spinner_item)
        val majorArray = resources.getStringArray(R.array.major)

        binding.psswrd.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.passwordAlrt.visibility= View.VISIBLE
                if (binding.psswrd.text.length>5) {
                    binding.passwordAlrt.visibility= View.INVISIBLE
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.doublecheck.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.doublecheckresult.visibility= View.VISIBLE
                if (binding.psswrd.text!=null && binding.doublecheck.text!=null) {
                    binding.signupButton.isEnabled =
                        ((binding.psswrd.text.toString() == binding.doublecheck.text.toString()) and (binding.psswrd.text.length>5))
                    if (binding.signupButton.isEnabled){
                        binding.doublecheckresult.text = "Password가 일치합니다!"
                        binding.doublecheckresult.setTextColor(Color.parseColor("#0000EE"))
                    }
                    else{
                        binding.doublecheckresult.text = "Password가 일치하지 않습니다!"
                        binding.doublecheckresult.setTextColor(Color.parseColor("#EE00FF"))
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })


        binding.signupButton.setOnClickListener{
            createAccount(binding.email.text.toString(),binding.psswrd.text.toString())
            Toast.makeText(this.applicationContext, "계정 생성 완료", Toast.LENGTH_LONG).show()
            createDatabase(majorArray[binding.majorSpinner.selectedItemPosition])
            finish()
        }


    }

    private fun createAccount(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this, "계정 생성 완료.",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this, "계정 생성 실패",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun createDatabase(s: String) {
        val newData = hashMapOf(
            "name" to binding.nameInput.text.toString(),
            "major" to s
        )
        db.collection("temp").document(binding.email.text.toString()).set(newData)
    }
}