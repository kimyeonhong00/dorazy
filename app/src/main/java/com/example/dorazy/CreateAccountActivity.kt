package com.example.dorazy

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.dorazy.databinding.CreateAccountBinding

private var auth: FirebaseAuth? = null

class CreateAccountActivity : AppCompatActivity(){

    private lateinit var binding:CreateAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.doublecheck.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.doublecheckresult.visibility= View.VISIBLE
                if (binding.psswrd.text!=null && binding.doublecheck.text!=null) {
                    binding.signupButton.isEnabled =
                        binding.psswrd.text.toString() == binding.doublecheck.text.toString()
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
                        finish() // 가입창 종료
                    } else {
                        Toast.makeText(
                            this, "계정 생성 실패",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}