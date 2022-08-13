package com.example.dorazy
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.dorazy.grouppage
import com.example.dorazy.GroupFragment
import com.example.dorazy.UserFragment
import com.example.dorazy.UserListFragment
import com.example.dorazy.databinding.ActivityGroupactivityBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.installations.FirebaseInstallations
import kotlinx.android.synthetic.main.activity_groupactivity.*
import kotlinx.android.synthetic.main.activity_grouppage.*

class GroupActivity : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    lateinit var mViewPager: ViewPager
    private var makeGroupBtn: AppCompatImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groupactivity)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        mViewPager = findViewById(R.id.container)
        container.adapter = mSectionsPagerAdapter
        makeGroupBtn = findViewById(R.id.makeGroupBtn)
        makeGroupBtn?.setOnClickListener {
            startActivity(Intent(it.context, grouppage::class.java))
        }
        val tabLayout: TabLayout = findViewById(R.id.tabs)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            @SuppressLint("RestrictedApi")
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 1) {     // char room
                    //makeGroupBtn.visibility = View.VISIBLE
                }
            }

            @SuppressLint("RestrictedApi")
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //makeGroupBtn.visibility = View.INVISIBLE
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        //GroupFragment()
        //container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        //tabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
        sendRegistrationToServer()

    }

    private fun sendRegistrationToServer() {
        val uid =
            FirebaseAuth.getInstance().currentUser!!.uid
        var token = ""
            FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Installations", "Installation ID: " + task.result)
                    token = task.result
                } else {
                    Log.e("Installations", "Unable to get Installation ID")
                }
            }
        var map = mutableMapOf<String, String?>()
        map["token"] = token
        FirebaseFirestore.getInstance().collection("User").document(uid).set(map, SetOptions.merge())
    }

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
*/
    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    class SectionsPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> GroupFragment()
                1 -> UserListFragment()
                else -> UserFragment()
            }
        }

        override fun getCount(): Int {
            return 3
        }
    }
    //}
}