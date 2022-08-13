package com.example.dorazy

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dorazy.grouppage
import com.example.dorazy.R
import com.example.dorazy.FirestoreAdapter
import com.example.dorazy.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
class UserListFragment : Fragment() {

    private var firestoreAdapter: FirestoreAdapter<*>? = null

    override fun onStart() {
        super.onStart()
        if (firestoreAdapter != null) {
            firestoreAdapter!!.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (firestoreAdapter != null) {
            firestoreAdapter!!.stopListening()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_userlist, container, false)
        firestoreAdapter = RecyclerViewAdapter(FirebaseFirestore.getInstance().collection("User").orderBy("name"))
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(inflater.context)
        recyclerView.adapter = firestoreAdapter
        return view
    }

    inner class RecyclerViewAdapter(query: Query):FirestoreAdapter<CustomViewHolder>(query) {
        //private val requestOptions = RequestOptions().transforms(CenterCrop(), RoundedCorners(90))
        private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
        private val myUid = FirebaseAuth.getInstance().currentUser!!.uid

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder = CustomViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false))
        override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {
            val documentSnapshot = getSnapshot(position)
            val user = documentSnapshot.toObject(UserModel::class.java)

            if (myUid == user!!.uid) {
                viewHolder.itemView.visibility = View.INVISIBLE
                viewHolder.itemView.layoutParams.height = 0
                return
            }
            viewHolder.user_name.text = user.name
            //viewHolder.user_msg.text = user.usermsg
        /*
            if (user.userphoto == null) {
                Glide.with(activity!!).load(R.drawable.user).apply(requestOptions).into(viewHolder.user_photo)
            } else {
                Glide.with(activity!!).load(storageReference.child("userPhoto/" + user.userphoto)).apply(requestOptions).into(viewHolder.user_photo)
            }*/

            viewHolder.itemView.setOnClickListener {
                val intent = Intent(view?.context, grouppage::class.java)
                intent.putExtra("toUid", user.uid)
                startActivity(intent)
            }
        }
    }

    inner class CustomViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        //var user_photo: ImageView = view.findViewById(R.id.user_photo)
        var user_name: TextView = view.findViewById(R.id.user_id)
        //var user_msg: TextView = view.findViewById(R.id.user_msg)
    }
}