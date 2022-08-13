package com.example.dorazy

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import android.app.ProgressDialog
import android.widget.TextView

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.example.dorazy.ChatModel
//import com.example.dorazy.Message
//import com.example.dorazy.NotificationModel
//import com.example.dorazy.ViewPagerActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
//import com.google.gson.Gson
//import okhttp3.*
//import okhttp3.Request.Builder
import java.util.*

//chatfragment
class fragment1:Fragment() {
    companion object {
        fun getInstance(groupID: String?): fragment1 {
            val thisfragment = fragment1()
            val bdl = Bundle()
            bdl.putString("groupID", groupID)
            thisfragment.arguments = bdl
            return thisfragment
        }
    }

    var groupID: String? = null
    var myUid: String? = null

    //var toUid: String? = null
    //var mAdapter: GroupRecyclerViewAdapter? = null
    @SuppressLint("SimpleDateFormat")
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")

    @SuppressLint("SimpleDateFormat")
    val dateFormatDay = SimpleDateFormat("yyyy-MM-dd")

    @SuppressLint("SimpleDateFormat")
    val dateFormatHour = SimpleDateFormat("aa hh:mm")

    val userList = hashMapOf<String, UserModel>()
    var listenerRegistration: ListenerRegistration? = null
    var firestore: FirebaseFirestore? = null
    var storageReference: StorageReference? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var progressDialog: ProgressDialog? = null
    var userCount = 0

    lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_group, container, false)


        linearLayoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = linearLayoutManager



        if (arguments != null) {
            groupID = arguments?.getString("groupID")
            //toUid = arguments?.getString("toUid")
        }
        firestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        dateFormatDay.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        dateFormatHour.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        myUid = FirebaseAuth.getInstance().currentUser?.uid

        /*if ("" != toUid && toUid != null) {                     // find existing group for two user
            findGroup(toUid!!)
        } else */
        if ("" != groupID && groupID != null) { // existing group (multi user)

            setGroup(groupID!!)
        }
        if (groupID == null) {                                                     // new group for two user

            getUserInfoFromServer(myUid)
            //getUserInfoFromServer(toUid)
            userCount = 2
        }

        /*recyclerView.addOnLayoutChangeListener { _, _, _, bottom, _, _, _, _, oldBottom ->
            if (bottom < oldBottom && mAdapter != null) {
                val lastAdapterItem = mAdapter!!.itemCount - 1
                recyclerView.post {
                    var recyclerViewPositionOffset = -1000000
                    val bottomView = linearLayoutManager?.findViewByPosition(lastAdapterItem)
                    if (bottomView != null) recyclerViewPositionOffset = 0 - bottomView.height
                    linearLayoutManager?.scrollToPositionWithOffset(
                        lastAdapterItem,
                        recyclerViewPositionOffset
                    )
                }
            }
        }*/
        return view
    }

    private fun findGroup(toUid: String) {
        firestore!!.collection("groups").whereGreaterThanOrEqualTo("users.$myUid", 0).get() //User
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                for (document in it.result!!) {
                    val users = document.get("users") as Map<String, Long>?
                    if (users?.size == 2 && users[toUid]?.toInt() != null) {
                        setGroup(document.id)
                        break
                    }
                }
            }
    }

    private fun setGroup(rid: String) {
        groupID = rid
        firestore?.collection("groups")?.document(groupID!!)?.get()?.addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }
            val document = it.result
            val users =
                document!!.get("users") as Map<String, Long>?
            for (key in users!!.keys) {
                getUserInfoFromServer(key)
            }
            userCount = users.size//users.put(myUid, (long) 0);
            document.getReference().update("users", users);

        }
    }

    private fun getUserInfoFromServer(id: String?) {
        firestore!!.collection("User").document(id!!).get().addOnSuccessListener {
            val userModel = it.toObject(UserModel::class.java)
            if (userModel?.uid != null) userList[userModel.uid!!] = userModel
            if (groupID != null && userCount == userList.size) {
                //mAdapter = GroupRecyclerViewAdapter()
                //recyclerView.adapter = mAdapter
            }
        }
    }

    fun showProgressDialog(title: String?) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(context)
        }
        progressDialog?.isIndeterminate = true
        progressDialog?.setTitle(title)
        progressDialog?.setMessage("Please wait..")
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    fun setProgressDialog(value: Int) {
        progressDialog!!.progress = value
    }

    fun hideProgressDialog() = progressDialog!!.dismiss()
    fun getUserList(): Map<String, UserModel> = userList

/*
    inner class GroupRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private var beforeDay: String? = null
        internal var beforeViewHolder: GroupInfoViewHolder? = null

        fun startListening() {
            beforeDay = null
            val roomRef: CollectionReference? =
                firestore?.collection("groups")?.document(groupID!!)?.collection("timestamp")
            // my chatting room information

        }

        fun stopListening() {
            if (listenerRegistration != null) {
                listenerRegistration?.remove()
                listenerRegistration = null
            }
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerView.ViewHolder {
            var view: View? = null
            view = LayoutInflater.from(parent.context)
                .inflate(viewType, parent, false)
            return GroupInfoViewHolder(view)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val groupInfoViewHolder: fragment1.GroupInfoViewHolder =
                holder as fragment1.GroupInfoViewHolder

                groupInfoViewHolder.msg_item?.text = ""
                groupInfoViewHolder.filename = ""
                groupInfoViewHolder.realname = ""
            }
            if (myUid != message.uid) {
                val userModel: UserModel? = userList[message.uid]
                messageViewHolder.msg_name?.text = userModel?.name
            }

            if (timestamp == null) {
                return
            }
            val day: String = dateFormatDay.format(timestamp)
            val timestamp: String = dateFormatHour.format(timestamp)
        groupInfoViewHolder.timestamp?.text = timestamp
        groupInfoViewHolder.timestamp.setText("");
        if (message.getTimestamp()==null) {return;}
        String day = dateFormatDay.format( message.getTimestamp());
        String timestamp = dateFormatHour.format( message.getTimestamp());
        groupInfoViewHolder.timestamp.setText(timestamp);

        beforeViewHolder = groupInfoViewHolder;

        override fun getItemCount() = 1//모임 수
    }

    inner class GroupInfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        /*var timestamp: TextView? = view.findViewById(R.id.timestamp)
        var groupTitle: TextView? = view.findViewById(R.id.groupTitle)
        var leader: TextView? = view.findViewById(R.id.groupLeader)
        var userCount: TextView? = view.findViewById(R.id.count)*/
        var username1: TextView ?= view.findViewById(R.id.leader)
        var username2: TextView ?= view.findViewById(R.id.member2)
        var username3: TextView ?= view.findViewById(R.id.member3)
        var username4: TextView ?= view.findViewById(R.id.member4)
        var username5: TextView ?= view.findViewById(R.id.member5)
        var username6: TextView ?= view.findViewById(R.id.member6)

        fun backPressed() {}

    }*/

}
