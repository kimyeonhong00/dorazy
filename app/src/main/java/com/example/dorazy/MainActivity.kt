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
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var auth :FirebaseAuth? = null
    private lateinit var binding:ActivityMainBinding

    var currentPosition=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.rankingbutton.setOnClickListener {
            startActivity(Intent(this, PersonalRankingActivity::class.java))
        }

        binding.bookingbutton.setOnClickListener {
            startActivity(Intent(this, MeetActivity::class.java))
        }

//        binding.logoutbutton.setOnClickListener {
//            val intent = Intent(this,LoginActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            startActivity(intent)
//            auth?.signOut()
//            finish()
//        }

        pager.adapter = ViewPagerAdapter(getList())
        pager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

    }

    private fun getList(): ArrayList<Int> {
        return arrayListOf<Int>(R.drawable.home_banner_info_1, R.drawable.home_banner_info_2, R.drawable.home_banner_seat)
    }

}
