package com.example.dorazy

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.dorazy.databinding.ActivitySelfstudyNextweekReservBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlin.concurrent.thread

class SelfstudyNextweekReservActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelfstudyNextweekReservBinding
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth : FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelfstudyNextweekReservBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        // 인텐트
        val selfstudyNextweekIntent = Intent(this, SelfstudyNextweekActivity::class.java)

        // 현재 예약 데이터 불러오기
        binding.reservBtn.isEnabled=false
        val reservStatus = ArrayList<List<String>>()
        val groupId = intent.getStringExtra("groupId")
        selfstudyNextweekIntent.putExtra("groupId",groupId)

        var maxPeriod = 5
        db.collection("groups").document(groupId!!).get().addOnSuccessListener {
            maxPeriod = when (it["userCount"].toString().toInt()){
                2,3,4 -> 4
                5 -> 5
                else -> 6
            }
        }
        var selected = arrayOf(0,0)
        var isSelected = false
        var period = 1
        var tp = -1
        db.collection("reservation").document("SelfStudySpace").get().addOnSuccessListener {doc ->
            val removeChars = "[] "
            var str = doc["mon1next"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            reservStatus.add(str.split(","))
            str = doc["tue1next"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            reservStatus.add(str.split(","))
            str = doc["wed1next"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            reservStatus.add(str.split(","))
            str = doc["thur1next"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            reservStatus.add(str.split(","))
            str = doc["fri1next"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            reservStatus.add(str.split(","))
            str = doc["sat1next"].toString()
            removeChars.forEach { str = str.replace(it.toString(),"") }
            reservStatus.add(str.split(","))
        }

        // 이용 시간 설정
        do {
            val spView: View = layoutInflater.inflate(R.layout.spinner_dialog, null)
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("시간 설정")
            builder.setMessage("몇 시간 예약하시겠습니까?\n(${maxPeriod.toFloat() / 2} 시간까지 이용가능합니다.)")
            builder.setView(spView)
            val sp: Spinner = spView.findViewById(R.id.time_spinner)
            sp.adapter = ArrayAdapter.createFromResource(
                this,
                R.array.use_time,
                R.layout.support_simple_spinner_dropdown_item
            )
            sp.setSelection(1)
            sp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    period = position + 1
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
            val listener = DialogInterface.OnClickListener { _, p1 ->
                when (p1) {
                    DialogInterface.BUTTON_POSITIVE -> {}
                    DialogInterface.BUTTON_NEGATIVE -> {}
                }
            }
            builder.setPositiveButton("예약하기", listener)
            builder.setNegativeButton("취소", listener)
            val dialog = builder.create()
            dialog.setCancelable(false)
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (period <= maxPeriod) {
                    Toast.makeText(
                        this,
                        "${period.toFloat() / 2}시간 예약을 진행합니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(
                        this,
                        "이용 불가능한 시간입니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                Toast.makeText(this, "취소하셨습니다.", Toast.LENGTH_SHORT).show()
                super.onBackPressed()
            }
        } while(period>maxPeriod)

        thread {
            Thread.sleep(1000)
            // 표 초기 세팅
            for (i in 0 until 6){
                for (j in 0 until 22){
                    val reservTeams = reservStatus[i][j].split("/").toMutableList()
                    val drId = resources.getIdentifier(toWeekString(i)+(j+1),"id",applicationContext.packageName)
                    val timetableButton = findViewById<Button>(drId)
                    runOnUiThread {
                        if(reservTeams[0]==""){
                            reservTeams.clear()
                        }
                        when (reservTeams.size) {
                            1 -> timetableButton.setBackgroundColor(Color.parseColor("#EDD74C"))
                            2 -> timetableButton.setBackgroundColor(Color.parseColor("#E09B53"))
                            3 -> {
                                timetableButton.setBackgroundColor(Color.parseColor("#F04C43"))
                                timetableButton.isEnabled = false
                            }
                        }
                    }

                }
            }
        }

        // 확인 눌렀을때
        fun reservClickYes(per:Int){
            val newStatus = reservStatus[selected[0]].toMutableList()
            for (i in selected[1]-1 until selected[1]+per-1) {
                if (newStatus[i].isEmpty()){
                    newStatus[i] = "$groupId"
                } else {
                    newStatus[i] = newStatus[i]+"/$groupId"
                }
            }
            db.collection("reservation").document("SelfStudySpace").update(toWeekString(selected[0])+"1next",newStatus)
            selfstudyNextweekIntent.putExtra("isReserved",true)
            selfstudyNextweekIntent.putExtra("groupId",groupId)
            startActivity(selfstudyNextweekIntent)
            finish()
        }

        // 예약하시겠습니까 다이얼로그
        fun showDialog() {
            //시간 계산
            var close = 23
            if (selected[0]==5){ close = 13 }
            var tempPeriod = period
            if (tp!=-1){
                tempPeriod = tp
            }
            if (tempPeriod>close-selected[1]){ tempPeriod = close-selected[1]}
            val stTime = 9 + (selected[1].toFloat()-1)/2
            val finTime = stTime + tempPeriod.toFloat()/2
            val st = arrayOf(stTime.toInt(),(stTime%1*60).toInt())
            val fin = arrayOf(finTime.toInt(),(finTime%1*60).toInt())
            val weekKor = when(selected[0]){
                0->"월"
                1->"화"
                2->"수"
                3->"목"
                4->"금"
                else->"토"
            }
            // AlertDialog 생성
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("예약 확인")
            builder.setMessage("${weekKor}요일 ${st[0]}:${st[1]}~${fin[0]}:${fin[1]} 예약하시겠습니까?")
            val listener = DialogInterface.OnClickListener { _, p1 ->
                when(p1) {
                    DialogInterface.BUTTON_POSITIVE ->
                        reservClickYes(tempPeriod)
                    DialogInterface.BUTTON_NEGATIVE ->
                        Toast.makeText(this, "취소하셨습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setPositiveButton("예", listener)
            builder.setNegativeButton("아니요", listener)
            builder.show()
        }


        // 예약 기능
        binding.reservBtn.setOnClickListener {
            showDialog()
        }

        // 뒤로 가기
        binding.backBtn.setOnClickListener {
            super.onBackPressed()
            finish()
        }

        // 화면 새로고침
        fun reload (w:Int,s:Int) {
            // w는 요일,s는 시작시간, isSelected은 선택(t) 취소(f) 여부
            isSelected = !isSelected
            var close = 23 // 도라지 닫는 시간
            if (w==5){ close = 13 } //토요일
            var tempPeriod = period // period는 바뀌면 안되므로 임시 period 생성
            if (tempPeriod>close-s){ // 닫는 시간까지만 이용 가능
                tempPeriod = close-s
            }
            if (isSelected) {
                selected = arrayOf(w,s)
                // 색칠
                binding.reservBtn.setBackgroundColor(Color.parseColor("#002244"))
                for (i in 0 until 6) {
                    for (j in 1 until 23) {
                        val btn = findViewById<Button>(resources.getIdentifier(toWeekString(i) + (j),"id",applicationContext.packageName))
                        btn.isEnabled = false
                    }
                }
                for (i in s until s + tempPeriod) {
                    val btn = findViewById<Button>(resources.getIdentifier(toWeekString(w) + (i),"id",applicationContext.packageName))
                    if (reservStatus[w][i-1].split("/").size==3){
                        Toast.makeText(this,"예약이 다 찬 시간대가 포함되었습니다.",Toast.LENGTH_SHORT).show()
                        tp = i-s
                        break
                    }
                    btn.setBackgroundColor(Color.parseColor("#002244"))
                    if (i == s+tempPeriod-1) {
                        tp = -1
                    }
                    btn.isEnabled = true
                }
                binding.reservBtn.isEnabled = true
            }
            else {
                binding.reservBtn.setBackgroundColor(Color.parseColor("#808080"))
                binding.reservBtn.isEnabled=false
                for (i in 0 until 6) {
                    for (j in 1 until 23) {
                        if ((i==5) and (j>12)){
                            continue
                        }
                        val btn = findViewById<Button>(resources.getIdentifier(toWeekString(i) + (j),"id",applicationContext.packageName))
                        val teams = if(reservStatus[i][j-1].split("/")[0]==""){
                            0
                        }else{
                            reservStatus[i][j-1].split("/").size
                        }
                        println("$i,$j,$teams")
                        btn.isEnabled = true
                        when (teams) {
                            3 -> btn.isEnabled = false
                            2 -> btn.setBackgroundColor(Color.parseColor("#E09B53"))
                            1 -> btn.setBackgroundColor(Color.parseColor("#EDD74C"))
                            else -> btn.setBackgroundColor(Color.parseColor("#00FFFFFF"))
                        }
                    }
                }
            }
        }

        // 타임 테이블 눌렀을 때
        //월
        binding.mon1.setOnClickListener {
            reload(0,1)
        }
        binding.mon2.setOnClickListener {
            reload(0,2)
        }
        binding.mon3.setOnClickListener {
            reload(0,3)
        }
        binding.mon4.setOnClickListener {
            reload(0,4)
        }
        binding.mon5.setOnClickListener {
            reload(0,5)
        }
        binding.mon6.setOnClickListener {
            reload(0,6)
        }
        binding.mon7.setOnClickListener {
            reload(0,7)
        }
        binding.mon8.setOnClickListener {
            reload(0,8)
        }
        binding.mon9.setOnClickListener {
            reload(0,9)
        }
        binding.mon10.setOnClickListener {
            reload(0,10)
        }
        binding.mon11.setOnClickListener {
            reload(0,11)
        }
        binding.mon12.setOnClickListener {
            reload(0,12)
        }
        binding.mon13.setOnClickListener {
            reload(0,13)
        }
        binding.mon14.setOnClickListener {
            reload(0,14)
        }
        binding.mon15.setOnClickListener {
            reload(0,15)
        }
        binding.mon16.setOnClickListener {
            reload(0,16)
        }
        binding.mon17.setOnClickListener {
            reload(0,17)
        }
        binding.mon18.setOnClickListener {
            reload(0,18)
        }
        binding.mon19.setOnClickListener {
            reload(0,19)
        }
        binding.mon20.setOnClickListener {
            reload(0,20)
        }
        binding.mon21.setOnClickListener {
            reload(0,21)
        }
        binding.mon22.setOnClickListener {
            reload(0,22)
        }


        //화
        binding.tue1.setOnClickListener {
            reload(1,1)
        }
        binding.tue2.setOnClickListener {
            reload(1,2)
        }
        binding.tue3.setOnClickListener {
            reload(1,3)
        }
        binding.tue4.setOnClickListener {
            reload(1,4)
        }
        binding.tue5.setOnClickListener {
            reload(1,5)
        }
        binding.tue6.setOnClickListener {
            reload(1,6)
        }
        binding.tue7.setOnClickListener {
            reload(1,7)
        }
        binding.tue8.setOnClickListener {
            reload(1,8)
        }
        binding.tue9.setOnClickListener {
            reload(1,9)
        }
        binding.tue10.setOnClickListener {
            reload(1,10)
        }
        binding.tue11.setOnClickListener {
            reload(1,11)
        }
        binding.tue12.setOnClickListener {
            reload(1,12)
        }
        binding.tue13.setOnClickListener {
            reload(1,13)
        }
        binding.tue14.setOnClickListener {
            reload(1,14)
        }
        binding.tue15.setOnClickListener {
            reload(1,15)
        }
        binding.tue16.setOnClickListener {
            reload(1,16)
        }
        binding.tue17.setOnClickListener {
            reload(1,17)
        }
        binding.tue18.setOnClickListener {
            reload(1,18)
        }
        binding.tue19.setOnClickListener {
            reload(1,19)
        }
        binding.tue20.setOnClickListener {
            reload(1,20)
        }
        binding.tue21.setOnClickListener {
            reload(1,21)
        }
        binding.tue22.setOnClickListener {
            reload(1,22)
        }


        //수
        binding.wed1.setOnClickListener {
            reload(2,1)
        }
        binding.wed2.setOnClickListener {
            reload(2,2)
        }
        binding.wed3.setOnClickListener {
            reload(2,3)
        }
        binding.wed4.setOnClickListener {
            reload(2,4)
        }
        binding.wed5.setOnClickListener {
            reload(2,5)
        }
        binding.wed6.setOnClickListener {
            reload(2,6)
        }
        binding.wed7.setOnClickListener {
            reload(2,7)
        }
        binding.wed8.setOnClickListener {
            reload(2,8)
        }
        binding.wed9.setOnClickListener {
            reload(2,9)
        }
        binding.wed10.setOnClickListener {
            reload(2,10)
        }
        binding.wed11.setOnClickListener {
            reload(2,11)
        }
        binding.wed12.setOnClickListener {
            reload(2,12)
        }
        binding.wed13.setOnClickListener {
            reload(2,13)
        }
        binding.wed14.setOnClickListener {
            reload(2,14)
        }
        binding.wed15.setOnClickListener {
            reload(2,15)
        }
        binding.wed16.setOnClickListener {
            reload(2,16)
        }
        binding.wed17.setOnClickListener {
            reload(2,17)
        }
        binding.wed18.setOnClickListener {
            reload(2,18)
        }
        binding.wed19.setOnClickListener {
            reload(2,19)
        }
        binding.wed20.setOnClickListener {
            reload(2,20)
        }
        binding.wed21.setOnClickListener {
            reload(2,21)
        }
        binding.wed22.setOnClickListener {
            reload(2,22)
        }


        //목
        binding.thur1.setOnClickListener {
            reload(3,1)
        }
        binding.thur2.setOnClickListener {
            reload(3,2)
        }
        binding.thur3.setOnClickListener {
            reload(3,3)
        }
        binding.thur4.setOnClickListener {
            reload(3,4)
        }
        binding.thur5.setOnClickListener {
            reload(3,5)
        }
        binding.thur6.setOnClickListener {
            reload(3,6)
        }
        binding.thur7.setOnClickListener {
            reload(3,7)
        }
        binding.thur8.setOnClickListener {
            reload(3,8)
        }
        binding.thur9.setOnClickListener {
            reload(3,9)
        }
        binding.thur10.setOnClickListener {
            reload(3,10)
        }
        binding.thur11.setOnClickListener {
            reload(3,11)
        }
        binding.thur12.setOnClickListener {
            reload(3,12)
        }
        binding.thur13.setOnClickListener {
            reload(3,13)
        }
        binding.thur14.setOnClickListener {
            reload(3,14)
        }
        binding.thur15.setOnClickListener {
            reload(3,15)
        }
        binding.thur16.setOnClickListener {
            reload(3,16)
        }
        binding.thur17.setOnClickListener {
            reload(3,17)
        }
        binding.thur18.setOnClickListener {
            reload(3,18)
        }
        binding.thur19.setOnClickListener {
            reload(3,19)
        }
        binding.thur20.setOnClickListener {
            reload(3,20)
        }
        binding.thur21.setOnClickListener {
            reload(3,21)
        }
        binding.thur22.setOnClickListener {
            reload(3,22)
        }


        //금
        binding.fri1.setOnClickListener {
            reload(4,1)
        }
        binding.fri2.setOnClickListener {
            reload(4,2)
        }
        binding.fri3.setOnClickListener {
            reload(4,3)
        }
        binding.fri4.setOnClickListener {
            reload(4,4)
        }
        binding.fri5.setOnClickListener {
            reload(4,5)
        }
        binding.fri6.setOnClickListener {
            reload(4,6)
        }
        binding.fri7.setOnClickListener {
            reload(4,7)
        }
        binding.fri8.setOnClickListener {
            reload(4,8)
        }
        binding.fri9.setOnClickListener {
            reload(4,9)
        }
        binding.fri10.setOnClickListener {
            reload(4,10)
        }
        binding.fri11.setOnClickListener {
            reload(4,11)
        }
        binding.fri12.setOnClickListener {
            reload(4,12)
        }
        binding.fri13.setOnClickListener {
            reload(4,13)
        }
        binding.fri14.setOnClickListener {
            reload(4,14)
        }
        binding.fri15.setOnClickListener {
            reload(4,15)
        }
        binding.fri16.setOnClickListener {
            reload(4,16)
        }
        binding.fri17.setOnClickListener {
            reload(4,17)
        }
        binding.fri18.setOnClickListener {
            reload(4,18)
        }
        binding.fri19.setOnClickListener {
            reload(4,19)
        }
        binding.fri20.setOnClickListener {
            reload(4,20)
        }
        binding.fri21.setOnClickListener {
            reload(4,21)
        }
        binding.fri22.setOnClickListener {
            reload(4,22)
        }

        //토
        binding.sat1.setOnClickListener {
            reload(5,1)
        }
        binding.sat2.setOnClickListener {
            reload(5,2)
        }
        binding.sat3.setOnClickListener {
            reload(5,3)
        }
        binding.sat4.setOnClickListener {
            reload(5,4)
        }
        binding.sat5.setOnClickListener {
            reload(5,5)
        }
        binding.sat6.setOnClickListener {
            reload(5,6)
        }
        binding.sat7.setOnClickListener {
            reload(5,7)
        }
        binding.sat8.setOnClickListener {
            reload(5,8)
        }
        binding.sat9.setOnClickListener {
            reload(5,9)
        }
        binding.sat10.setOnClickListener {
            reload(5,10)
        }
        binding.sat11.setOnClickListener {
            reload(5,11)
        }
        binding.sat12.setOnClickListener {
            reload(5,12)
        }

    }

    //숫자를 요일로
    private fun toWeekString(n : Int) :String{
        return when (n){
            0-> "mon"
            1-> "tue"
            2-> "wed"
            3-> "thur"
            4-> "fri"
            else -> "sat"
        }
    }
}