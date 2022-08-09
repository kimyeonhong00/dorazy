package com.example.dorazy


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.dorazy.R
import com.example.dorazy.helpers
import com.example.dorazy.UserModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
@Suppress("DEPRECATION")
class UserFragment : Fragment() {

    companion object {
        private val PICK_FROM_ALBUM = 1
    }

    // private var user_photo: ImageView? = null
    private var user_id: EditText? = null
    private var user_name: EditText? = null
    // private var user_msg: EditText? = null
    private var userModel: UserModel? = null
    private var userPhotoUri: Uri? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        user_id?.isEnabled = false
        getUserInfoFromServer()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*user_photo?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(intent, PICK_FROM_ALBUM)
        }*/

        /*saveBtn.setOnClickListener{
            if (!validateForm()) return@setOnClickListener

            userModel?.usernm = user_name!!.text.toString()
            userModel?.usermsg =  user_msg!!.text.toString()
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val db = FirebaseFirestore.getInstance()

            if (userPhotoUri != null)   userModel!!.userphoto = uid

            db.collection("users").document(uid)
                .set(userModel!!)
                .addOnSuccessListener {
                    if (userPhotoUri == null) {helpers.showMessage(activity!!, "Success to Save.")}
                    else {
                        // small image
                        Glide.with(context!!)
                            .asBitmap().load(userPhotoUri).apply(RequestOptions().override(150, 150))
                            .into(object : SimpleTarget<Bitmap>() {
                                override fun onResourceReady(
                                    bitmap: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    val baos = ByteArrayOutputStream()
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                                    val data = baos.toByteArray()
                                    FirebaseStorage.getInstance().reference.child("userPhoto/$uid")
                                        .putBytes(data)
                                    helpers.showMessage(activity!!, "Success to Save.")
                                }
                            })
                    }
                }
        }*/


        /*changePWBtn.setOnClickListener {
            startActivity(Intent(activity, UserPWActivity::class.java))
        }*/

    }

    private fun getUserInfoFromServer() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        val docRef = FirebaseFirestore.getInstance().collection("users").document(uid)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            userModel = documentSnapshot.toObject<UserModel>(UserModel::class.java)
            user_id?.setText(userModel?.user_id)
            user_name?.setText(userModel?.name)
            //user_msg?.setText(userModel?.usermsg)
            /*if (userModel?.userphoto != null && "" != userModel?.userphoto) {
                Glide.with(activity!!).load(FirebaseStorage.getInstance().getReference("userPhoto/" + userModel?.userphoto)).into(user_photo!!)
            }*/
        }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_FROM_ALBUM && resultCode == Activity.RESULT_OK) {
            user_photo!!.setImageURI(data?.data)
            userPhotoUri = data?.data
        }
    }*/

    /*private fun validateForm(): Boolean {
        var valid = true

        val userName = user_name!!.text.toString()
        if (TextUtils.isEmpty(userName)) {
            user_name?.error = "Required."
            valid = false
        } else {
            user_name?.error = null
        }

        *//*val userMsg = user_msg?.text.toString()
        if (TextUtils.isEmpty(userMsg)) {
            user_msg?.error = "Required."
            valid = false
        } else {
            user_msg?.error = null
        }
        helpers.hideKeyboard(activity!!)*//*

        return valid
    }*/

}