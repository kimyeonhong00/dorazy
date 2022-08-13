package com.example.dorazy

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import android.view.View.OnClickListener
import android.view.Gravity
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.in_group.*
import java.text.SimpleDateFormat

class groupDetail : AppCompatActivity() { //chatactivity
    private var drawerLayout: DrawerLayout? = null
    //private var fragment: fragment1? = null
    private var userListInGroupFragment: UserListInGroupFragment? = null
    //private var rightMenuBtn: Button?= null //AppCompatImageButton
    private var backBtn: AppCompatImageButton?= null
    private var gtitle: TextView ? = null
    private var hText1 : TextView ? = null
    private var hText2: TextView ? = null
    private var memText1: TextView ? = null
    private var memText2: TextView ? = null
    private var memText3: TextView ? = null
    private var memText4: TextView ? = null
    private var memText5: TextView ? = null
    private var memText6: TextView ? = null
    private var checkBtn: Button ? = null
    private var moreBtn: Button ? = null
    private var resBtn: Button ? = null
    private var lentBtn: Button ? = null
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    var groupID: String? = null
    var myUid: String? = null
    @SuppressLint("SimpleDateFormat")
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
    @SuppressLint("SimpleDateFormat")
    val dateFormatDay = SimpleDateFormat("yyyy-MM-dd")
    @SuppressLint("SimpleDateFormat")
    val dateFormatHour = SimpleDateFormat("aa hh:mm")
    val userList1 = hashMapOf<String, UserModel>()
    var userCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment1)
        val groupID: String? = intent?.getStringExtra("groupID")
        val groupTitle: String? = intent?.getStringExtra("groupTitle")
        val groupLeader: String? = intent?.getStringExtra("groupLeader")
        gtitle= findViewById(R.id.gtitle)
        gtitle?.text = groupTitle
        memText1 = findViewById(R.id.member1)
        memText2 = findViewById(R.id.member2)
        memText3 = findViewById(R.id.member3)
        memText4 = findViewById(R.id.member4)

        firestore = FirebaseFirestore.getInstance()
        if (groupID != null) {
            setGroup(groupID)
        }
        backBtn = findViewById(R.id.groupBack)
        checkBtn = findViewById(R.id.checkButton)
        resBtn = findViewById(R.id.imageButton)
        lentBtn = findViewById(R.id.imageButton2)
        moreBtn = findViewById(R.id.moreButton)


        resBtn?.setOnClickListener{
            val intent1 = Intent(this,MainActivity::class.java)//이부분예약페이지이름으로 편경
            intent1.putExtra("groupID",groupID)
            startActivity(intent1)
        }
        backBtn?.setOnClickListener {
            onBackPressed()
        }
        // left drawer
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        checkBtn = findViewById(R.id.checkButton)
        checkBtn?.setOnClickListener(OnClickListener {
            val intent = Intent(this@groupDetail, grouppage::class.java)
            /*startActivity(intent)
            if (drawerLayout!!.isDrawerOpen(Gravity.RIGHT)) {
                drawerLayout!!.closeDrawer(Gravity.RIGHT)
            } else {
                if (userListInGroupFragment == null) {
                    println("userListIngroupfragment userList")
                    println(userList1)
                    userListInGroupFragment = UserListInGroupFragment.getInstance(groupID!!, userList1)
                    supportFragmentManager.beginTransaction().replace(R.id.drawerFragment, userListInGroupFragment!!).commit()
                }
                drawerLayout!!.openDrawer(Gravity.RIGHT)
            }*/
        })


        println("userList1 - main")
        println(userList1)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        finish()
    }
    private fun getLeaderInfoFromServer(uid: String?): String? {
        var leader_name : String ? = null
        firestore!!.collection("User").document(uid!!).get().addOnSuccessListener {
            val userModel = it.toObject(UserModel::class.java)
            if(userModel?.uid != null) leader_name = userModel?.name
        }
        return leader_name
    }
    private fun setGroup(rid: String) {
        groupID = rid
        firestore.collection("groups").document(groupID!!).get()
            .addOnSuccessListener { document ->
                val users = document!!.get("users") as Map<String, UserModel>
                println("setgroup users ")
                println(users.keys)
                for(key in users.keys){
                    getUserInfoFromServer(key)
                }
                userCount= document!!.get("userCount").toString().toInt()
                println("setGroup Success")
            }.addOnFailureListener{ e ->
                Log.d(TAG, "exception ", e)
            }


    }
    private fun getUser(id: String?){
        firestore.collection("User").document(id!!).get().addOnSuccessListener { document->
            val str = document["uid"].toString()
            println(str)
            println("유저 가져옴!")
        }
    }
    private fun getUserInfoFromServer(id: String?) {
        firestore.collection("User").document(id!!).get().addOnSuccessListener {
            val userModel = it.toObject(UserModel::class.java)
            if(userModel?.uid != null)  userList1[userModel.uid!!] = userModel
            if (groupID != null /*&& userCount == userList1.size*/) {
                println("getuserinfofromserver success")
                println("userList1 - getUserInfo")
                println(userList1)
                println(userList1.keys)
            }
        }
    }
    private fun getUserInfoFromServer1(id: String?) {
        userList1.clear()
        firestore!!.collection("User").document(id!!).get().addOnSuccessListener { document ->
            println(document)
            println("document")
            if(document != null){
                val userModel1 = UserModel()
                userModel1.user_id = document.data?.get("user_id")?.toString()
                userModel1.uid = document.data?.get("uid")?.toString()
                userModel1.name = document.data?.get("name")?.toString()
                println(document.data?.get("user_id")?.toString())
                println(document.data?.get("uid")?.toString())
                println(document.data?.get("name")?.toString())
                println()

                userList1[userModel1.uid!!] = userModel1
                println("userList1.keys")
                println(userList1.keys)
            }
            else{
                Log.d(TAG,"NO SUCH DOCUMENT")
            }

        }.addOnFailureListener{ exception ->
                Log.d(TAG, "get failed with ",exception)
        }
    }

}
