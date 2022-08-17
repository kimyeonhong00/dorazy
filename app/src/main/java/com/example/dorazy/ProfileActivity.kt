package com.example.dorazy

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.dorazy.databinding.ActivityProfileBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile.*
import com.example.dorazy.databinding.EditDescLayoutBinding


class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var database: FirebaseFirestore

    var percent = 0
    var introduce = intent.getStringExtra("Desc")

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 바인딩 초기화(코틀린 파일에서 값 수정하기 위해 필요)


        val cu = Firebase.auth.currentUser
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val saveuser = db.collection("User").document(cu!!.uid)

        if (saveuser != null) {
            saveuser.get()
                .addOnSuccessListener { document ->
                    if ( document != null){
                        Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                        introduce = document.data!!["introduce"].toString()

                    } else{
                        Log.d(ContentValues.TAG, "No such document")
                    }
                }
                .addOnFailureListener{ exception ->
                    Log.d(ContentValues.TAG, "get failed with ",exception)
                }
        }


        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_profile)
        database = Firebase.firestore



        database.collection("User").get().addOnSuccessListener { doc ->
            for (d in doc){
                // 현재 사용자의 시간을 DB에서 받아 text 변경하기
                if (d.id== cu!!.uid) {

                    binding.profileName.text = d.data["name"].toString()
                    var userStudyTime = d.data["studyTime"].toString().toInt()
                    var goalTime = d.data["goalTime_db"].toString().toInt()

                    // 백분율: (진행시간/목표시간)*100
                    percent =
                        if (userStudyTime>goalTime)
                            100
                        else
                            (userStudyTime/goalTime)*100

                    binding.percent.text = "${percent}%"
                }

            }
        }

        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.imgAdd.setOnClickListener{
            when {
                // 갤러리 접근 권한이 있는 경우
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                -> {
                    navigateGallery()
                }

                // 갤러리 접근 권한이 없는 경우 & 교육용 팝업을 보여줘야 하는 경우
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                -> {
                    showPermissionContextPopup()
                }

                // 권한 요청 하기(requestPermissions) -> 갤러리 접근(onRequestPermissionResult)
                else -> requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1000
                )
            }
        }
        // 부제 수정 기능
        binding.subname.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val builderItem = EditDescLayoutBinding.inflate(layoutInflater)
            val editText = builderItem.editDesc

            with(builder){
                setTitle("소개")
                setMessage("자신에 대해 소개해보세요")
                setView(builderItem.root)
                setPositiveButton("OK"){ dialogInterface: DialogInterface, i: Int ->
                    if(editText.text != null){
                        if (saveuser != null) {
                            introduce = editText.text.toString()
                            saveuser.update(
                                mapOf("introduce" to introduce.toString())
                            )
                        }
                    }
                }
                show()
            }
        }

    }

    // 권한 요청 승인 이후 실행되는 함수
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    navigateGallery()
                else
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                //
            }
        }
    }

    private fun navigateGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        // 가져올 컨텐츠들 중에서 Image 만을 가져온다.
        intent.type = "image/*"
        // 갤러리에서 이미지를 선택한 후, 프로필 이미지뷰를 수정하기 위해 갤러리에서 수행한 값을 받아오는 startActivityForeResult를 사용한다.
        startActivityForResult(intent, 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 예외처리
        if (resultCode != Activity.RESULT_OK)
            return

        when (requestCode) {
            // 2000: 이미지 컨텐츠를 가져오는 액티비티를 수행한 후 실행되는 Activity 일 때만 수행하기 위해서
            2000 -> {
                val selectedImageUri: Uri? = data?.data
                if (selectedImageUri != null) {
                    binding.profileImage.setImageURI(selectedImageUri)
                }
                else {
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("프로필 이미지를 바꾸기 위해서는 갤러리 접근 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }




    private fun toast(message:String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}

