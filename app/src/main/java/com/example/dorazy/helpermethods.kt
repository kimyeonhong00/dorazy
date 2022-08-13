package com.example.dorazy

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.text.SimpleDateFormat
import java.util.*

object helpers {



    fun showMessage(context: Context, msg: String) {
        val toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }
/*
    val uniqueValue: String
        @SuppressLint("SimpleDateFormat")
        get() {
            val ft = SimpleDateFormat("yyyyMMddhhmmssSSS")
            return ft.format(Date()) + (Math.random() * 10).toInt()
        }

    val rootPath: String
        get() {
            val sdPath: String
            val ext1 = Environment.getExternalStorageState()
            if (ext1 == Environment.MEDIA_MOUNTED) {
                sdPath = Environment.getExternalStorageDirectory().absolutePath
            } else {
                sdPath = Environment.MEDIA_UNMOUNTED
            }
            return sdPath
        }
    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun size2String(filesize: Long): String {
        val unit = 1024
        if (filesize < unit) {
            return String.format("%d bytes", filesize)
        }
        val exp = (Math.log(filesize!!.toDouble()) / Math.log(unit.toDouble())).toInt()

        return String.format(
            "%.0f %sbytes", filesize / Math.pow(unit.toDouble(), exp.toDouble()),
            "KMGTPE"[exp - 1]
        )
    }

    @SuppressLint("ObsoleteSdkInt")
    fun isPermissionGranted(activity: Activity, permission: String): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                Log.v("Dorazy", "Permission is granted")
                return true
            } else {
                Log.v("Dorazy", "Permission is revoked")
                ActivityCompat.requestPermissions(activity, arrayOf(permission), 1)
                return false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Dorazy", "Permission is granted")
            return true
        }
    }*/
}