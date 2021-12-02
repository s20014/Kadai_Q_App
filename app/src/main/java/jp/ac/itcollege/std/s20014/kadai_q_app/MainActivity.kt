package jp.ac.itcollege.std.s20014.kadai_q_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.lifecycleScope
import jp.ac.itcollege.std.s20014.kadai_q_app.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var _helper: DatabaseHelper
    private lateinit var oldVersion: String
    companion object{
        private const val QUIZ_URL = "https://script.google.com/macros/s/AKfycbznWpk2m8q6lbLWSS6qaz3uS6j3L4zPwv7CqDEiC433YOgAdaFekGJmjoAO60quMg6l/exec?f="
        private const val VERSION = "version"
        private const val DATA = "data"
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val pref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        oldVersion = pref.getString("version", "null").toString()

        setContentView(binding.root)
        binding.startButton.setOnClickListener {
            next()
        }





    }

    override fun onResume() {
        super.onResume()
        getVersion(QUIZ_URL + VERSION)


    }

    @UiThread
    private fun getVersion(url: String) {

        lifecycleScope.launch {
            val result = getJson(url)
            getVersionPost(result)
        }
    }

    @UiThread
    private fun getData(url: String) {
        lifecycleScope.launch {
            val result = getJson(url)
            getDataPost(result)
        }
    }

    @WorkerThread
    private suspend fun getJson(url: String): String {
        val res = withContext(Dispatchers.IO) {
            var result = ""
            val con = URL(url).openConnection() as? HttpURLConnection
            con?.let {
                try {
                    it.connectTimeout = 10000
                    it.readTimeout = 10000
                    it.requestMethod = "GET"
                    it.connect()

                    val stream = it.inputStream
                    result = extendString(stream)
                    stream.close()
                } catch(ex: SocketTimeoutException) {
                    println("通信タイムアウト")
                }
                it.disconnect()
            }
            result
        }
        return res
    }

    private fun extendString(stream: InputStream?) : String {
        val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
        return reader.readText()
    }

    @UiThread
    private fun getVersionPost(result: String) {
        val newVersion = JSONObject(result).getString("version")
        if (oldVersion != newVersion) {

            _helper = DatabaseHelper(this)
            val db = _helper.writableDatabase
            val delete = """
                DELETE FROM quiz;
            """.trimIndent()
            val stmt = db.compileStatement(delete)
            stmt.executeUpdateDelete()

            val pref = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
            pref.edit().putString("version", newVersion).apply()
            getData(QUIZ_URL + DATA)
        }


    }

    @UiThread
    private fun getDataPost(result: String) {
        val rootData = JSONArray(result)
        val num = rootData.length()
        _helper = DatabaseHelper(this)

        val db = _helper.writableDatabase

        val insert = """
                INSERT INTO quiz
                (_id, question, answers, quiz1, quiz2, quiz3, quiz4, quiz5, quiz6)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent()

        val stmt = db.compileStatement(insert)


        for (i in 0 until num) {
            val id = rootData.getJSONObject(i).getLong("id")
            val question = rootData.getJSONObject(i).getString("question")
            val answers = rootData.getJSONObject(i).getLong("answers")
            val choices = rootData.getJSONObject(i).getJSONArray("choices")
            val q1 = choices[0].toString()
            val q2 = choices[1].toString()
            val q3 = choices[2].toString()
            val q4 = choices[3].toString()
            val q5 = choices[4].toString()
            val q6 = choices[5].toString()


            stmt.run {
                bindLong(1, id)
                bindString(2, question)
                bindLong(3, answers)
                bindString(4, q1)
                bindString(5, q2)
                bindString(6, q3)
                bindString(7, q4)
                bindString(8, q5)
                bindString(9, q6)

            }
            stmt.executeInsert()

        }
    }


    fun next() {
        val intent = Intent(this, QuizView::class.java)
        startActivity(intent)
    }
}