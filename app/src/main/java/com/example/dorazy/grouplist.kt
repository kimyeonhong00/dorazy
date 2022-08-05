package com.example.dorazy

import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBar
import android.view.View.OnClickListener
import android.view.Gravity
import android.view.MenuItem
import android.widget.Button

class grouplist : AppCompatActivity() { //chatactivity
    private var drawerLayout: DrawerLayout? = null
    //private var chatFragment: ChatFragment? = null
    private var userListInGroupFragment: UserListInGroupFragment? = null
    private var rightMenuBtn: Button?= null
    private var backBtn: Button?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grouplist)
        val toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar)
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
        rightMenuBtn?.setOnClickListener(OnClickListener {
            if (drawerLayout!!.isDrawerOpen(Gravity.RIGHT)) {
                drawerLayout!!.closeDrawer(Gravity.RIGHT)
            } else {
                if (userListInGroupFragment == null) {
                    userListInGroupFragment = UserListInGroupFragment.getInstance(groupID!!, chatFragment!!.userList)
                    supportFragmentManager.beginTransaction().replace(R.id.drawerFragment, userListInGroupFragment!!).commit()
                }
                drawerLayout!!.openDrawer(Gravity.RIGHT)
            }
        })
        backBtn?.setOnClickListener {
            onBackPressed()
        }
        // chatting area
        chatFragment = ChatFragment.getInstance(toUid, groupID)
        supportFragmentManager.beginTransaction().replace(R.id.mainFragment, chatFragment!!).commit()
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
        chatFragment?.backPressed()
        finish()
    }
}