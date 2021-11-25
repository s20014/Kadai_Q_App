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
    private val QUIZ_URL = "https://script.google.com/macros/s/AKfycbznWpk2m8q6lbLWSS6qaz3uS6j3L4zPwv7CqDEiC433YOgAdaFekGJmjoAO60quMg6l/exec?f="


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        getVersion(QUIZ_URL + "version")
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
            val url = URL(url)
            val con = url.openConnection() as? HttpURLConnection
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
        binding.versionView.text = newVersion
        getData(QUIZ_URL + "data")
    }

    @UiThread
    private fun getDataPost(result: String) {
        val rootData = JSONArray(result)
        val num = rootData.length()
        val id = rootData.getJSONObject(0).getLong("id")
        val question = rootData.getJSONObject(0).getString("question")
        val answers = rootData.getJSONObject(0).getLong("answers")
        val choices = rootData.getJSONObject(0).getJSONArray("choices")
        val Q1 = choices[0].toString()
        val Q2 = choices[1].toString()
        val Q3 = choices[2].toString()
        val Q4 = choices[3].toString()
        val Q5 = choices[4].toString()
        val Q6 = choices[5].toString()

        _helper = DatabaseHelper(this)

        val db = _helper.writableDatabase

        val insert = """
            INSERT INTO quiz
            (_id, question, answers, Q1, Q2, Q3, Q4, Q5, Q6)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        val stmt = db.compileStatement(insert)

        stmt.run {
            bindLong(1,id)
            bindString(2, question)
            bindLong(3, answers)
            bindString(4, Q1)
            bindString(5, Q2)
            bindString(6, Q3)
            bindString(7, Q4)
            bindString(8, Q5)
            bindString(9, Q6)

            stmt.executeInsert()
        }
    }

    override fun onDestroy() {
      //  _helper.close()
        super.onDestroy()
    }
}