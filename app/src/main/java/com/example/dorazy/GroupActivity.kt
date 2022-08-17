package com.example.dorazy
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.dorazy.grouppage
import com.example.dorazy.GroupFragment
import com.example.dorazy.UserFragment
import com.example.dorazy.UserListFragment
import com.example.dorazy.databinding.ActivityGroupactivityBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.installations.FirebaseInstallations
import kotlinx.android.synthetic.main.activity_groupactivity.*
import kotlinx.android.synthetic.main.activity_grouppage.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.api.Distribution
import java.lang.Thread.sleep
import kotlin.concurrent.thread

class GroupActivity : AppCompatActivity() {
    @SuppressLint("SimpleDateFormat")
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private var mAdapter: RecyclerViewAdapter? = null
    private var makeGroupBtn: AppCompatImageButton ?= null
    var call: Int ? = null
    var temp = ""
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groupactivity)
        makeGroupBtn = findViewById(R.id.makeGroupBtn)
        val toolbar:Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = RecyclerViewAdapter()
        recyclerView.adapter= mAdapter
        makeGroupBtn?.setOnClickListener{
            startActivity(Intent(it.context,grouppage::class.java))
        }

        simpleDateFormat.timeZone= TimeZone.getTimeZone("Asia/Seoul")
        // 어느 예약페이지에서 보냈는지 알 수 있게 하는 변수
        call = intent.getIntExtra("call",0)

        firestore.collection("User").document("53Uj3d9cAFbVYiiXBkEQHH4Bxm82").get()
            .addOnSuccessListener { document ->
                println("document - > {$document}")
                println(document.data)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mAdapter != null) mAdapter!!.stopListening()
    }
    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val groupList = ArrayList<GroupModel>()
        private val userList = HashMap<String, UserModel>()
        private val myUid: String = FirebaseAuth.getInstance().currentUser!!.uid
        private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        private var listenerRegistration: ListenerRegistration? = null
        private var listenerUsers: ListenerRegistration? = null
        override fun getItemCount(): Int = groupList.size
        init {
            // all users information
            listenerUsers = firestore.collection("User")
                .addSnapshotListener(EventListener{value, e->
                if (e != null) { return@EventListener }
                for (doc in value!!) { userList[doc.id] = doc.toObject<UserModel>(UserModel::class.java) }
                getGroupInfo()
            })
        }
        
        fun getGroupInfo() {
            val db = firestore.collection("User")
            listenerRegistration =
                firestore.collection("groups").whereGreaterThanOrEqualTo("users.$myUid", -1)
                    .addSnapshotListener(EventListener{value, e->
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
                            db.document(document.get("leader").toString()).get()
                                .addOnSuccessListener { d ->
                                    groupModel.leader = d.data?.get("name").toString()
                                }
                                .addOnFailureListener{ e ->
                                    Log.d(TAG, "GET failed with "+e)
                                    //groupModel.leader =document.get("leader").toString()
                                }
                            groupModel.title = document.getString("title")

                            if (groupModel.timestamp == null) groupModel.timestamp = Date()
                            orderedGroups[groupModel.timestamp!!] = groupModel
                        }
                        groupList.clear()
                        for ((_, value1) in orderedGroups) {
                            groupList.add(value1)
                        }
                        notifyDataSetChanged()
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
            groupViewHolder.group_title.text= groupModel.title
            groupViewHolder.group_leader.text= groupModel.leader
            groupViewHolder.group_count.text= groupModel.userCount.toString()
            if (groupModel.userCount!! > 0) {
                groupViewHolder.group_count.text= groupModel.userCount.toString()
                groupViewHolder.group_count.visibility= View.VISIBLE
            } else {
                groupViewHolder.group_count.visibility= View.INVISIBLE
            }
            groupViewHolder.itemView.setOnClickListener{v->
                val intent = Intent(v.context, groupDetail::class.java)

                intent.putExtra("call",call)
                intent.putExtra("groupID", groupModel.groupID)
                intent.putExtra("groupTitle", groupModel.title)
                intent.putExtra("groupLeader",groupModel.leader)
                startActivity(intent)
            }
        }

        private inner class GroupViewHolder internal constructor(view: View) :
            RecyclerView.ViewHolder(view) {
            var group_title: TextView = view.findViewById(R.id.group_title)
            var last_time: TextView = view.findViewById(R.id.last_time)
            var group_count: TextView = view.findViewById(R.id.group_count)
            //var unread_count: TextView = view.findViewById(R.id.unread_count)
            var group_leader: TextView = view.findViewById(R.id.group_leader)
        }
    }
}
