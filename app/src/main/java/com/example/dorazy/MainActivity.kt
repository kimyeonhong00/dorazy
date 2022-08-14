package com.example.dorazy

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dorazy.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var auth :FirebaseAuth? = null
    private lateinit var binding:ActivityMainBinding
    var backPressedTime: Long = 0

    private var currentPosition=0
    val handler=Handler(Looper.getMainLooper()){
        setPage()
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.rankingbutton.setOnClickListener {
            startActivity(Intent(this, PersonalRankingActivity::class.java))
        }

        binding.groupList.setOnClickListener {
            startActivity(Intent(this, GroupActivity::class.java))
        }
        binding.bookingbutton.setOnClickListener {
            startActivity(Intent(this, MeetActivity::class.java))
        }

        binding.recordbutton.setOnClickListener {
        }

        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayShowTitleEnabled(true)
        pager.adapter = ViewPagerAdapter(getList())
        pager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val thread=Thread(PagerRunnable())
        thread.start()
    }

    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        if (System.currentTimeMillis() - backPressedTime < 2000) {
            finish()
            return
        }

        Toast.makeText(this, "뒤로 버튼을 한 번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
        backPressedTime = System.currentTimeMillis()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                return super.onOptionsItemSelected(item)
            }
            R.id.action_power -> {
                val intent = Intent(this,LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                auth?.signOut()
                finish()

                return super.onOptionsItemSelected(item)
            }
            else -> return super.onOptionsItemSelected(item)

        }
    }

    private fun getList(): ArrayList<Int> {
        return arrayListOf(R.drawable.home_banner_info_1, R.drawable.home_banner_info_2, R.drawable.home_banner_seat)
    }

    private fun setPage(){
        if(currentPosition==5) currentPosition=0
        pager.setCurrentItem(currentPosition,true)
        currentPosition+=1
    }

    //2초 마다 페이지 넘기기
    inner class PagerRunnable:Runnable{
        override fun run() {
            while(true){
                Thread.sleep(2000)
                handler.sendEmptyMessage(0)
            }
        }
    }
}
