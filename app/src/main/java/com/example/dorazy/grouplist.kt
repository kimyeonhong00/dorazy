package com.example.dorazy

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBar
import android.view.View.OnClickListener
import android.view.Gravity
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton

class grouplist : AppCompatActivity() { //chatactivity
    private var drawerLayout: DrawerLayout? = null
    private var fragment: fragment1? = null
    private var userListInGroupFragment: UserListInGroupFragment? = null
    private var rightMenuBtn: AppCompatImageButton?= null
    private var backBtn: Button?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grouplist)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeButtonEnabled(true)

        val toUid: String? = intent?.getStringExtra("toUid")
        val groupID: String? = intent?.getStringExtra("groupID")
        val groupTitle: String? = intent?.getStringExtra("groupTitle")
        actionBar.setTitle(groupTitle)
        // left drawer
        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        rightMenuBtn = findViewById(R.id.rightMenuBtn)
        rightMenuBtn?.setOnClickListener(OnClickListener {
            val intent = Intent(this@grouplist, grouppage::class.java)
            startActivity(intent)
            if (drawerLayout!!.isDrawerOpen(Gravity.RIGHT)) {
                drawerLayout!!.closeDrawer(Gravity.RIGHT)
            } else {
                if (userListInGroupFragment == null) {
                    userListInGroupFragment = UserListInGroupFragment.getInstance(groupID!!, fragment!!.userList)
                    supportFragmentManager.beginTransaction().replace(R.id.drawerFragment, userListInGroupFragment!!).commit()
                }
                drawerLayout!!.openDrawer(Gravity.RIGHT)
            }
        })
        backBtn?.setOnClickListener {
            onBackPressed()
        }
        // chatting area
        // 여기가 그룹 detail 나오는 fragment 불러오는 페이지
        fragment = fragment1.getInstance(toUid, groupID)
        supportFragmentManager.beginTransaction().replace(R.id.mainFragment, fragment!!).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        fragment?.backPressed()
        finish()
    }
}