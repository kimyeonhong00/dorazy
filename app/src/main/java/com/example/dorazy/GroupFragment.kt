package com.example.dorazy

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
//import kotlinx.android.synthetic.main.fragment_chatroom.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class GroupFragment : Fragment() { //Chatroomfragment
    companion object {
        fun setBadge(context: Context, count: Int) {
            val launcherClassName = getLauncherClassName(context) ?: return
            val intent = Intent("android.intent.action.BADGE_COUNT_UPDATE")
            intent.putExtra("badge_count", count)
            intent.putExtra("badge_count_package_name", context.packageName)
            intent.putExtra("badge_count_class_name", launcherClassName)
            context.sendBroadcast(intent)
        }
        fun getLauncherClassName(context: Context): String? {
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            val resolveInfos = pm.queryIntentActivities(intent, 0)
            for (resolveInfo in resolveInfos) {
                val pkgName = resolveInfo.activityInfo.applicationInfo.packageName
                if (pkgName.equals(context.packageName, ignoreCase = true)) {
                    return resolveInfo.activityInfo.name
                }
            }
            return null
        }
    }

    @SuppressLint("SimpleDateFormat")
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private var mAdapter: RecyclerViewAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.item_fragment, container, false) //여기 레이아웃 item chat
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        println("GroupFragment 호출")
        recyclerView.layoutManager = LinearLayoutManager(inflater.context)
        mAdapter = RecyclerViewAdapter()
        recyclerView.adapter = mAdapter
        simpleDateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mAdapter != null) {mAdapter!!.stopListening()}
    }

    // =============================================================================================
    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val groupList = ArrayList<GroupModel>()
        private val userList = HashMap<String, UserModel>()
        private val myUid: String = FirebaseAuth.getInstance().currentUser!!.uid
        private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        private var listenerRegistration: ListenerRegistration? = null
        private var listenerUsers: ListenerRegistration? = null
        override fun getItemCount(): Int = groupList.size
        init {
            // all users information                          //users
            listenerUsers = firestore.collection("User").addSnapshotListener(EventListener { value, e ->
                if (e != null) { return@EventListener }
                for (doc in value!!) { userList[doc.id] = doc.toObject<UserModel>(UserModel::class.java) }
                getGroupInfo()
            })
        }
        fun getGroupInfo() {
            listenerRegistration =
                firestore.collection("groups").whereGreaterThanOrEqualTo("users.$myUid", -1)
                    .addSnapshotListener(EventListener { value, e ->
                        if (e != null) {
                            return@EventListener
                        }
                        val orderedGroups =
                            TreeMap<Date, GroupModel>(Collections.reverseOrder<Any>())

                        for (document in value!!) {

                            val groupModel = GroupModel()
                            groupModel.groupID = document.id
                            val users = document.get("users") as Map<String, Long>?
                            groupModel.userCount = (users!!.size)
                            groupModel.leader = document.get("leader").toString()

                            // group chat room
                            groupModel.title = document.getString("title")

                            if (groupModel.timestamp == null) groupModel.timestamp = Date()
                            orderedGroups[groupModel.timestamp!!] = groupModel
                        }
                        groupList.clear()
                        for ((_, value1) in orderedGroups) {
                            groupList.add(value1)
                        }
                        notifyDataSetChanged()
                        //setBadge(context!!, unreadTotal!!)
                    })
        }

        fun stopListening() {
            if (listenerRegistration != null) {
                listenerRegistration!!.remove()
                listenerRegistration = null
            }
            if (listenerUsers != null) {
                listenerUsers!!.remove()
                listenerUsers = null
            }

            groupList.clear()
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
            return GroupViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val groupViewHolder = holder as GroupViewHolder
            val groupModel = groupList[position]
            groupViewHolder.group_title.text = groupModel.title
            groupViewHolder.group_leader.text = groupModel.leader
            groupViewHolder.group_count.text = groupModel.userCount.toString()
            if (groupModel.userCount!! > 2) {
                groupViewHolder.group_count.text = groupModel.userCount.toString()
                groupViewHolder.group_count.visibility = View.VISIBLE
            } else {
                groupViewHolder.group_count.visibility = View.INVISIBLE
            }

            groupViewHolder.itemView.setOnClickListener { v ->
                val intent = Intent(v.context, groupDetail::class.java)
                intent.putExtra("groupID", groupModel.groupID)
                intent.putExtra("groupTitle", groupModel.title)
                intent.putExtra("groupLeader",groupModel.leader)
                startActivity(intent)
            }
        }

        private inner class GroupViewHolder internal constructor(view: View) :
            RecyclerView.ViewHolder(view) {
            //var room_image: ImageView = view.findViewById(R.id.room_image)
            var group_title: TextView = view.findViewById(R.id.group_title)
            var last_time: TextView = view.findViewById(R.id.last_time)
            var group_count: TextView = view.findViewById(R.id.group_count)
            var unread_count: TextView = view.findViewById(R.id.unread_count)
            var group_leader: TextView = view.findViewById(R.id.group_leader)
        }
    }
}