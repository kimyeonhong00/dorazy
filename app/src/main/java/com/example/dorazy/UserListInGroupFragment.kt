package com.example.dorazy
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.dorazy.grouppage
import com.example.dorazy.R
import com.example.dorazy.UserModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UserListInGroupFragment : Fragment() {
    companion object {
        fun getInstance(groupID: String, userModels: Map<String, UserModel>): UserListInGroupFragment {
            val users = mutableListOf<UserModel>()
            for ((_, value) in userModels) {users.add(value)}
            val f = UserListInGroupFragment()
            f.setUserList(users)
            val bdl = Bundle()
            bdl.putString("groupID", groupID)
            f.arguments = bdl
            return f
        }
    }

    private var groupID: String? = null
    private var userModels: List<UserModel>? = null
    private var recyclerView: RecyclerView? = null
    private var addContactBtn: Button? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_userlistinroom, container, false)
        if (arguments != null) {groupID = arguments?.getString("groupID")}

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView!!.layoutManager = LinearLayoutManager(inflater.context)
        recyclerView!!.adapter = UserFragmentRecyclerViewAdapter()

        addContactBtn?.setOnClickListener {
            val intent = Intent(activity, grouppage::class.java)
            intent.putExtra("groupID", groupID)
            startActivity(intent)
        }

        return view
    }

    fun setUserList(users: List<UserModel>) {
        userModels = users
    }

    internal inner class UserFragmentRecyclerViewAdapter :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
        private val requestOptions = RequestOptions().transforms(CenterCrop(), RoundedCorners(90))

        override fun getItemCount() = userModels!!.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
            return CustomViewHolder(view)
        }
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val user = userModels!![position]
            val customViewHolder = holder as CustomViewHolder
            customViewHolder.user_name.text = user.name

        }
    }

    private inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var user_id: TextView = view.findViewById(R.id.user_id)
        var user_name: TextView = view.findViewById(R.id.user_id2)
        /*init {
            user_msg.visibility = View.GONE
        }*/
    }
}
