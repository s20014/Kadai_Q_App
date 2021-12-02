package jp.ac.itcollege.std.s20014.kadai_q_app

import android.annotation.SuppressLint
import android.content.Intent
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
    private var allQuizList = arrayListOf<List<Any>>()
    private val idList = arrayListOf<Long>()

    private var i = 0
    private var score = 0
    private var totalTime = 0L
    private var answer = mutableListOf<Int>()
    private var userAnswer = mutableListOf<Int>()

    private val timer = TimeLeftCountdown(10L * 1000, 100L)
    private var nowTime = 0L

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

    private fun click(n :Int) {
        if (n in userAnswer) {
            userAnswer.remove(n)
            toPurple(n)
        } else {
            userAnswer.add(n)
            toGray(n)
        }
    }

    private fun toGray(n: Int) {
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

    private fun toPurple(n: Int) {
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

        val ids = """
            SELECT _id FROM quiz
        """.trimIndent()

        val arrayId = db.rawQuery(ids, null)
        if (arrayId.count > 0) {
            arrayId.moveToFirst()
            while (!arrayId.isAfterLast) {
                idList.add(arrayId.getLong(0))
                arrayId.moveToNext()
            }
        }
        arrayId.close()

        val ram = idList.toList().shuffled()

        for (i in 0 until 10) {
            val randomId = (ram[i]).toString()
            val cursor = db.rawQuery(sql, arrayOf(randomId))
            println(arrayId)

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
                allQuizList.add(mutableListOf(id, question, answers, quizList))
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
            nowTime = 10000 - millisUntilFinished
        }

        override fun onFinish() {
            nowTime = 10000L
            result()
        }
    }

    fun result() {

        timer.cancel()

        totalTime += nowTime

        binding.okButton.isEnabled = false
        val handler = Handler(Looper.getMainLooper())
        if (userAnswer.sorted() == answer.sorted()) {
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

    private fun print() {
        timer.start()
        binding.okButton.isEnabled = true

        for (j in 0 until 6) {
            toPurple(j)
        }

        binding.imageView.visibility = View.GONE

        userAnswer = mutableListOf()
        answer = mutableListOf()

        binding.idView.text = allQuizList[i][0].toString()
        binding.questionView.text = allQuizList[i][1].toString()

        val temp = allQuizList[i][3] as List<*>
        val choices = temp.shuffled()

        for (j in 0 until (allQuizList[i][2] as Int)){
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

    private fun moveToNext() {
        i++
        if (i >= 10) {
            val intent = Intent(this, ResultView::class.java)
            intent.apply {
                putExtra("score", score)
                putExtra("time", totalTime)
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

