package com.example.dorazy

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.EditText
import kotlinx.android.synthetic.main.lent_dialog.*

class LentDialog(context: Context) {
    private val dialog = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener
    fun setOnClickListener(listener: OnDialogClickListener){
        onClickListener=listener
    }
    fun showDialog(){
        dialog.setContentView(R.layout.lent_dialog)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()
        val student = dialog.findViewById<EditText>(R.id.studentID)
        dialog.conFirmBtn.setOnClickListener {
            onClickListener.onClicked(student.text.toString())
            dialog.dismiss()
        }
    }
    interface OnDialogClickListener{
        fun onClicked(name: String)
    }
}