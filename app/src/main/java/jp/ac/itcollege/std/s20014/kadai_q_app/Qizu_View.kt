package jp.ac.itcollege.std.s20014.kadai_q_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.core.os.HandlerCompat
import jp.ac.itcollege.std.s20014.kadai_q_app.databinding.ActivityMainBinding
import jp.ac.itcollege.std.s20014.kadai_q_app.databinding.ActivityQizuViewBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.util.concurrent.Executors

class Qizu_View : AppCompatActivity() {
    private lateinit var binding: ActivityQizuViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQizuViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}

