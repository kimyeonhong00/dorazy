package com.example.dorazy

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import android.app.ProgressDialog

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
        fun getInstance(toUid: String?, groupID: String?): fragment1 {
            val thisfragment = fragment1()
            val bdl = Bundle()
            bdl.putString("toUid", toUid)
            bdl.putString("groupID", groupID)
            thisfragment.arguments = bdl
            return thisfragment
        }
    }

    var groupID: String? = null
    var myUid: String? = null
    var toUid: String? = null
    //var mAdapter: ChatRecyclerViewAdapter? = null
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
            toUid = arguments?.getString("toUid")
        }
        firestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        dateFormatDay.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        dateFormatHour.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        myUid = FirebaseAuth.getInstance().currentUser?.uid

        if ("" != toUid && toUid != null) {                     // find existing group for two user
            findGroup(toUid!!)
        } else if ("" != groupID && groupID != null) { // existing group (multi user)

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
        firestore!!.collection("groups").whereGreaterThanOrEqualTo("User.$myUid", 0).get()
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
                document!!.get("User") as Map<String, Long>?
            for (key in users!!.keys) {
                getUserInfoFromServer(key)
            }
            userCount = users.size//users.put(myUid, (long) 0);
            document.getReference().update("User", users);

        }
    }
    private fun getUserInfoFromServer(id: String?) {
        firestore!!.collection("User").document(id!!).get().addOnSuccessListener {
            val userModel = it.toObject(UserModel::class.java)
            if(userModel?.uid != null)  userList[userModel.uid!!] = userModel
            if (groupID != null && userCount == userList.size) {
                //mAdapter = ChatRecyclerViewAdapter()
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
    fun backPressed() {}

    /*inner class ChatRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val requestOptions = RequestOptions().transforms(CenterCrop(), RoundedCorners(90))
        internal var messageList: MutableList<Message>? = null
        private var beforeDay: String? = null
        internal var beforeViewHolder: MessageViewHolder? = null


        fun startListening() {
            beforeDay = null
            messageList?.clear()
            val roomRef: CollectionReference? =
                firestore?.collection("rooms")?.document(roomID!!)?.collection("messages")
            // my chatting room information

            listenerRegistration = roomRef?.orderBy("timestamp")?.addSnapshotListener { p0, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                var message: Message
                for (change in p0!!.documentChanges) {
                    when (change.type) {
                        Type.ADDED -> {
                            message = change.document.toObject<Message>(Message::class.java)
                            if (message.readUsers.indexOf(myUid) == -1) {
                                message.readUsers.add(myUid)
                                change.document.reference
                                    .update("readUsers", message.getReadUsers())
                            }
                            messageList?.add(message)
                            notifyItemInserted(change.newIndex)
                        }
                        Type.MODIFIED -> {
                            message = change.document.toObject<Message>(
                                Message::class.java
                            )
                            messageList?.set(change.oldIndex, message)
                            notifyItemChanged(change.oldIndex)
                        }
                        Type.REMOVED -> {
                            messageList?.removeAt(change.oldIndex)
                            notifyItemRemoved(change.oldIndex)
                        }
                    }
                }
                recyclerView.scrollToPosition(messageList!!.size - 1)

            }
        }

        fun stopListening() {
            if (listenerRegistration != null) {
                listenerRegistration?.remove()
                listenerRegistration = null
            }
            messageList?.clear()
            notifyDataSetChanged()
        }

        override fun getItemViewType(position: Int): Int {
            val message: Message = messageList!![position]
            return if (myUid == message.uid) {
                when (message.msgtype) {
                    "1" -> R.layout.item_chatimage_right
                    "2" -> R.layout.item_chatfile_right
                    else -> R.layout.item_chatmsg_right
                }
            } else {
                when (message.getMsgtype()) {
                    "1" -> R.layout.item_chatimage_left
                    "2" -> R.layout.item_chatfile_left
                    else -> R.layout.item_chatmsg_left
                }
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerView.ViewHolder {
            var view: View? = null
            view = LayoutInflater.from(parent.context)
                .inflate(viewType, parent, false)
            return MessageViewHolder(view)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val messageViewHolder: ChatFragment.MessageViewHolder =
                holder as ChatFragment.MessageViewHolder
            val message: Message = messageList!![position]

            if(messageViewHolder.read_counter != null)
                setReadCounter(message, messageViewHolder.read_counter!!)

            if ("0" == message.msgtype) {                                      // text message
                messageViewHolder.msg_item?.text = message.msg
            } else if ("2" == message.msgtype) {                                      // file transfer

                messageViewHolder.msg_item?.text = message.filename.toString() + "\n" + message.filesize
                messageViewHolder.filename = message.filename
                messageViewHolder.realname = message.msg
                val file =
                    File(rootPath + message.filename)
                if (file.exists()) {
                    messageViewHolder.button_item?.text = "Open File"
                } else {
                    messageViewHolder.button_item?.text = "Download"
                }
            } else {                                                                // image transfer
                messageViewHolder.realname = message.msg
                if(messageViewHolder.img_item != null)
                    Glide.with(context!!)
                        .load(storageReference?.child("filesmall/" + message.msg))
                        .apply(RequestOptions().override(1000, 1000))
                        .into(messageViewHolder.img_item!!)
            }
            if (myUid != message.uid) {
                val userModel: UserModel? = userList[message.uid]
                messageViewHolder.msg_name?.text = userModel?.usernm
                if (userModel?.userphoto == null) {
                    Glide.with(context!!).load(R.drawable.user)
                        .apply(requestOptions)
                        .into(messageViewHolder.user_photo!!)
                } else {
                    Glide.with(context!!)
                        .load(storageReference?.child("userPhoto/" + userModel.userphoto))
                        .apply(requestOptions)
                        .into(messageViewHolder.user_photo!!)
                }
            }
            messageViewHolder.divider?.visibility = View.INVISIBLE
            messageViewHolder.divider?.layoutParams?.height = 0
            messageViewHolder.timestamp?.text = ""
            if (message.timestamp == null) {
                return
            }
            val day: String = dateFormatDay.format(message.timestamp)
            val timestamp: String = dateFormatHour.format(message.timestamp)
            messageViewHolder.timestamp?.text = timestamp
            if (position == 0) {
                messageViewHolder.divider_date?.text = day
                messageViewHolder.divider?.visibility = View.VISIBLE
                messageViewHolder.divider?.layoutParams?.height = 60
            }
            /*messageViewHolder.timestamp.setText("");
        if (message.getTimestamp()==null) {return;}
        String day = dateFormatDay.format( message.getTimestamp());
        String timestamp = dateFormatHour.format( message.getTimestamp());
        messageViewHolder.timestamp.setText(timestamp);
        if (position==0) {
            messageViewHolder.divider_date.setText(day);
            messageViewHolder.divider.setVisibility(View.VISIBLE);
            messageViewHolder.divider.getLayoutParams().height = 60;
        };
        if (!day.equals(beforeDay) && beforeDay!=null) {
            beforeViewHolder.divider_date.setText(beforeDay);
            beforeViewHolder.divider.setVisibility(View.VISIBLE);
            beforeViewHolder.divider.getLayoutParams().height = 60;
        }
        beforeViewHolder = messageViewHolder;
        beforeDay = day;*/ else {
                val beforeMsg: Message = messageList!![position - 1]
                val beforeDay: String = dateFormatDay.format(beforeMsg.timestamp)
                if (day != beforeDay && beforeDay != null) {
                    messageViewHolder.divider_date?.text = day
                    messageViewHolder.divider?.visibility = View.VISIBLE
                    messageViewHolder.divider?.layoutParams?.height = 60
                }
            }
        }

        private fun setReadCounter(message: Message, textView: TextView) {
            val cnt: Int = userCount - message.readUsers.size
            if (cnt > 0) {
                textView.visibility = View.VISIBLE
                textView.text = cnt.toString()
            } else {
                textView.visibility = View.INVISIBLE
            }
        }

        override fun getItemCount() = messageList!!.size

        init {
            val dir = File(rootPath)
            if (!dir.exists()) {
                if (!helpers.isPermissionGranted(
                        activity!!,
                        permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {

                } else dir.mkdirs()
            }
            messageList = ArrayList<Message>()
            setUnread2Read()
            startListening()
        }
    }


    inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var user_photo: ImageView? = view.findViewById(R.id.user_photo)
        var msg_item: TextView? = view.findViewById(R.id.msg_item)
        var img_item: ImageView? = view.findViewById(R.id.img_item) // only item_chatimage_
        var msg_name: TextView? = view.findViewById(R.id.msg_name)
        var timestamp: TextView? = view.findViewById(R.id.timestamp)
        var read_counter: TextView? = view.findViewById(R.id.read_counter)
        var divider: LinearLayout? = view.findViewById(R.id.divider)
        var divider_date: TextView? = view.findViewById(R.id.divider_date)
        var button_item: TextView? = view.findViewById(R.id.button_item) // only item_chatfile_
        var msgLine_item: LinearLayout? = view.findViewById(R.id.msgLine_item) // only item_chatfile_
        var filename: String = ""
        var realname: String = ""


        var downloadClickListener = object : View.OnClickListener {
            override fun onClick(view: View) {
                if ("Download" == button_item?.text) {
                    download()
                } else {
                    openWith()
                }
            }

            fun download() {
                if (!helpers.isPermissionGranted(activity!!, WRITE_EXTERNAL_STORAGE)
                ) {
                    return
                }
                showProgressDialog("Downloading File.")
                val localFile = File(rootPath, filename)
                storageReference?.child("files/" + realname)?.getFile(localFile)?.addOnSuccessListener{
                    button_item?.text = "Open File"
                    hideProgressDialog()
                    Log.e("DirectTalk9 ", "local file created $localFile")
                }?.addOnFailureListener{
                    Log.e("DirectTalk9 ", "local file not created $it")
                }
            }

            @SuppressLint("ObsoleteSdkInt")
            fun openWith() {
                val newFile = File(rootPath + filename)
                val mime = MimeTypeMap.getSingleton()
                val ext = newFile.name.substring(newFile.name.lastIndexOf(".") + 1)
                val type = mime.getMimeTypeFromExtension(ext)
                val intent = Intent(Intent.ACTION_VIEW)
                val uri: Uri
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(context!!,activity!!.packageName + ".provider", newFile)
                    val resInfoList = activity!!.packageManager
                        .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                    for (resolveInfo in resInfoList) {
                        val packageName = resolveInfo.activityInfo.packageName
                        activity!!.grantUriPermission(packageName,uri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    }
                } else {
                    uri = Uri.fromFile(newFile)
                }
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(uri, type)//"application/vnd.android.package-archive");
                startActivity(Intent.createChooser(intent, "Your title"))
            }
        }
        // photo view
        private var imageClickListener = View.OnClickListener {
            val intent = Intent(context, ViewPagerActivity::class.java)
            intent.putExtra("roomID", roomID)
            intent.putExtra("realname", realname)
            startActivity(intent)
        }

        // file download and open
        init {
            // for file
            msgLine_item?.setOnClickListener(downloadClickListener)
            img_item?.setOnClickListener(imageClickListener)
        }


    }*/
}