package com.example.dorazy

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.content.Intent
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.dorazy.FirestoreAdapter
import com.google.firebase.*
import androidx.recyclerview.widget.*
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.*
import com.google.firebase.firestore.DocumentChange.Type
import com.google.firebase.firestore.FieldValue.serverTimestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class grouppage : AppCompatActivity() { //selectuser
    private var groupID: String?= null
    val selectedUsers = mutableMapOf<String, String>()
    var firestoreAdapter: FirestoreAdapter<*>? = null
    var firestore: FirebaseFirestore? = null

    override fun onStart(){
        super.onStart()
        if (firestoreAdapter != null){
            firestoreAdapter!!.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (firestoreAdapter != null){
            firestoreAdapter!!.stopListening()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grouppage)

        groupID = intent.getStringExtra("groupID")
        println("~~~~~~~~~~~~groupID getstring extra")
        firestoreAdapter = RecyclerViewAdapter(FirebaseFirestore.getInstance().collection("User").orderBy("name"))
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        recyclerView.setAdapter(firestoreAdapter)
        val makeGroupBtn: Button = findViewById(R.id.makeGroupBtn)
        if (groupID == null) makeGroupBtn.setOnClickListener(makeGroupClickListener) else makeGroupBtn.setOnClickListener(
            addGroupUserClickListener
        )
    }
    private var makeGroupClickListener =
        View.OnClickListener {
            if (selectedUsers.size < 2) {
                helpers.showMessage(applicationContext, "Please select 2 or more user")
                return@OnClickListener
            }
            selectedUsers[FirebaseAuth.getInstance().currentUser!!.uid] = ""
            val newGroup =
                FirebaseFirestore.getInstance()
                    .collection("groups").document()
            CreateGroup(newGroup)
        }
    private var addGroupUserClickListener =
        View.OnClickListener {
            if (selectedUsers.isEmpty()) {
                helpers.showMessage(applicationContext, "Please select 1 or more user")
                return@OnClickListener
            }
            CreateGroup(
                FirebaseFirestore.getInstance().collection(
                    "groups"
                ).document(groupID!!)
            )
        }


    private fun CreateGroup(group: DocumentReference){
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val users = mutableMapOf<String, Int>()
        val str = EditText(this)
        var title = ""
        val a = System.currentTimeMillis()
        val timestamp = Date()
        val t_date = Date(a)
        val t_dateFormat = SimpleDateFormat("yyyy-MM-dd kk:mm:ss E", Locale("ko", "KR"))
        val str_date = t_dateFormat.format(t_date)
        str.gravity = Gravity.CENTER
        var builder = AlertDialog.Builder(this).setTitle("그룹 이름을 입력하세요")
            .setView(str).setPositiveButton("확인",
                DialogInterface.OnClickListener{dialog, which ->
                    when(which){
                        DialogInterface.BUTTON_POSITIVE -> {title = str.text.toString()
                            Toast.makeText(this,str.text, Toast.LENGTH_SHORT).show()}

                    }

                })
        builder.show()
        if(title.length==0){
            for (key in selectedUsers.keys){
                users[key] = 0
                if(title.length<20 && (key!=uid)){
                    title +=selectedUsers[key].toString()+", "
                }
            }
            title =title.substring(0,title.length-2)+str_date
        }
        val data = mutableMapOf<String, Any>()
        data["timestamp"] = serverTimestamp()
        data["title"]= title
        data["users"] = users
        data["leader"] = uid
        data["userCount"] = users.size
        group.set(data).addOnCompleteListener {
            if(it.isSuccessful){
                println("~~~~~~~~~~~~데이터세팅 성공~~~~~\n")
                val intent = Intent(this@grouppage, groupDetail::class.java)
                intent.putExtra("groupID", group.id)
                intent.putExtra("groupTitle", title)
                startActivity(intent)
                this@grouppage.finish()
            }
        }
    }

    inner class RecyclerViewAdapter(query: Query?):FirestoreAdapter<CustomViewHolder>(query) {
        private val requestOptions: RequestOptions = RequestOptions().transforms(CenterCrop(), RoundedCorners(90))
        private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
        private val myUid = FirebaseAuth.getInstance().currentUser!!.uid

        @NonNull
        override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): CustomViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_select_user, parent, false)
            return CustomViewHolder(view)
        }

        override fun onBindViewHolder(@NonNull viewHolder: CustomViewHolder, position: Int) {
            val documentSnapshot: DocumentSnapshot =
                getSnapshot(position)
            val userModel: UserModel = documentSnapshot.toObject<UserModel>(UserModel::class.java)!!
            if (myUid == userModel.uid) {
                viewHolder.itemView.visibility = View.INVISIBLE
                viewHolder.itemView.layoutParams.height = 0
                return
            }

            viewHolder.user_id.text = userModel.user_id
            viewHolder.itemView.context

            viewHolder.userChk.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedUsers[userModel.uid!!] = userModel.user_id!!
                } else {
                    selectedUsers.remove(userModel.uid)
                }
            }
        }

    }
    class CustomViewHolder internal constructor(view: View) :
        RecyclerView.ViewHolder(view) {
        var user_id: TextView = view.findViewById(R.id.user_id)
        var userChk: CheckBox = view.findViewById(R.id.userChk)
        //var user_photo: ImageView = view.findViewById(R.id.user_photo)
    }
}
