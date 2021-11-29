package jp.ac.itcollege.std.s20014.kadai_q_app

import android.annotation.SuppressLint
import android.content.Intent
import android.database.DatabaseUtils
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import androidx.core.os.postDelayed
import jp.ac.itcollege.std.s20014.kadai_q_app.databinding.ActivityQizuViewBinding


class QuizView : AppCompatActivity() {

    private lateinit var binding: ActivityQizuViewBinding
    private lateinit var _helper: DatabaseHelper
    private var allquizlist = arrayListOf<List<Any>>()

    private var i = 0
    private var score = 0
    private var totaltime = 0L
    private var answer = mutableListOf<Int>()
    private var useranswer = mutableListOf<Int>()

    private val timer = TimeLeftCountdown(10L * 1000, 100L)
    private var nowtime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQizuViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        _helper = DatabaseHelper(this)



        binding.run {
            q0.setOnClickListener { click(0) }
            q1.setOnClickListener { click(1) }
            q2.setOnClickListener { click(2) }
            q3.setOnClickListener { click(3) }
            q4.setOnClickListener { click(4) }
            q5.setOnClickListener { click(5) }

        }
        binding.okButton.setOnClickListener {
            result()
        }
    }

    fun click(n :Int) {
        if (n in useranswer) {
            useranswer.remove(n)
            toParple(n)
        } else {
            useranswer.add(n)
            toGray(n)
        }
    }

    fun toGray(n: Int) {
        val color = Color.rgb(200, 200, 200)
        when(n) {
            0 -> binding.q0.setBackgroundColor(color)
            1 -> binding.q1.setBackgroundColor(color)
            2 -> binding.q2.setBackgroundColor(color)
            3 -> binding.q3.setBackgroundColor(color)
            4 -> binding.q4.setBackgroundColor(color)
            5 -> binding.q5.setBackgroundColor(color)
        }
    }

    fun toParple(n: Int) {
        val color = Color.rgb(0, 0, 255)
        when(n) {
            0 -> binding.q0.setBackgroundColor(color)
            1 -> binding.q1.setBackgroundColor(color)
            2 -> binding.q2.setBackgroundColor(color)
            3 -> binding.q3.setBackgroundColor(color)
            4 -> binding.q4.setBackgroundColor(color)
            5 -> binding.q5.setBackgroundColor(color)
        }
    }

    @SuppressLint
    override fun onResume() {
        super.onResume()
        val db = _helper.writableDatabase
        val sql = """
            SELECT * FROM quiz 
            WHERE _id = ?
        """.trimIndent()
        val dbsize = DatabaseUtils.queryNumEntries(db, "quiz").toInt()
        val randomInt = (1..dbsize).toList().shuffled()

        for (i in 0 until 10) {
            val randomId = (1000 + randomInt[i]).toString()
            val cursor = db.rawQuery(sql, arrayOf(randomId))

            while (cursor.moveToNext()) {
                val id = cursor.let {
                    val index = it.getColumnIndex("_id")
                    it.getString(index)
                }
                val question = cursor.let {
                    val index = it.getColumnIndex("question")
                    it.getString(index)
                }
                val answers = cursor.let {
                    val index = it.getColumnIndex("answers")
                    it.getInt(index)
                }
                val quiz1 = cursor.let {
                    val index = it.getColumnIndex("quiz1")
                    it.getString(index)
                }
                val quiz2 = cursor.let {
                    val index = it.getColumnIndex("quiz2")
                    it.getString(index)
                }
                val quiz3 = cursor.let {
                    val index = it.getColumnIndex("quiz3")
                    it.getString(index)
                }
                val quiz4 = cursor.let {
                    val index = it.getColumnIndex("quiz4")
                    it.getString(index)
                }
                val quiz5 = cursor.let {
                    val index = it.getColumnIndex("quiz5")
                    it.getString(index)
                }
                val quiz6 = cursor.let {
                    val index = it.getColumnIndex("quiz6")
                    it.getString(index)
                }
                val quizList = ArrayList<String>()
                quizList.run {
                    add(quiz1)
                    add(quiz2)
                    add(quiz3)
                    add(quiz4)
                }

                if (quiz5 != ""){ quizList.add(quiz5) }
                if (quiz6 != ""){ quizList.add(quiz6) }
                allquizlist.add(mutableListOf(id, question, answers, quizList))
            }
            cursor.close()
        }
        print()

    }

    override fun onDestroy() {
        _helper.close()
        super.onDestroy()
    }

    inner class  TimeLeftCountdown(minPast: Long, countInterval: Long): CountDownTimer(minPast, countInterval) {

        override fun onTick(millisUntilFinished: Long) {
            binding.timeLeftBar.progress = (millisUntilFinished / 100).toInt()
            nowtime = 10000 - millisUntilFinished
        }

        override fun onFinish() {
            nowtime = 10000L
            result()
        }
    }

    fun result() {

        timer.cancel()

        totaltime += nowtime

        binding.okButton.isEnabled = false
        val handler = Handler(Looper.getMainLooper())
        if (useranswer.sorted() == answer.sorted()) {
            ++score
            binding.imageView.setImageResource(R.drawable.maru)
            binding.imageView.visibility = View.VISIBLE
        }else {
            binding.imageView.setImageResource(R.drawable.batu)
            binding.imageView.visibility = View.VISIBLE
        }

        handler.postDelayed(2000L) {
            moveToNext()
        }
    }

    fun print() {
        timer.start()
        binding.okButton.isEnabled = true

        for (j in 0 until 6) {
            toParple(j)
        }

        binding.imageView.visibility = View.GONE

        useranswer = mutableListOf()
        answer = mutableListOf()

        binding.idView.text = allquizlist[i][0].toString()
        binding.questionView.text = allquizlist[i][1].toString()

        val temp = allquizlist[i][3] as List<*>
        val choices = temp.shuffled()

        for (j in 0 until (allquizlist[i][2] as Int)){
            answer.add(choices.indexOf(temp[j]))
        }

        binding.run {
            q0.text = choices[0].toString()
            q1.text = choices[1].toString()
            q2.text = choices[2].toString()
            q3.text = choices[3].toString()
        }
        when(choices.size) {
            4 -> {
                binding.q4.visibility = View.GONE
                binding.q5.visibility = View.GONE
            }
            5 -> {
                binding.q4.visibility = View.VISIBLE
                binding.q4.text = choices[4].toString()
                binding.q5.visibility = View.GONE
            }
            else -> {
                binding.q4.visibility = View.VISIBLE
                binding.q5.visibility = View.VISIBLE
                binding.q4.text = choices[4].toString()
                binding.q5.text = choices[5].toString()
            }
        }
    }

    fun moveToNext() {
        i++
        if (i >= 1) {
            val intent = Intent(this, ResultView::class.java)
            intent.apply {
                putExtra("score", score)
                putExtra("time", totaltime)
            }
            startActivity(intent)
        } else {
            print()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        return true
    }


}

