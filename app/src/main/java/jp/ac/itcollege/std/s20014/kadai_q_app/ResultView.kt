package jp.ac.itcollege.std.s20014.kadai_q_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import jp.ac.itcollege.std.s20014.kadai_q_app.databinding.ActivityResultViewBinding

class ResultView : AppCompatActivity() {
    private lateinit var binding: ActivityResultViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityResultViewBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val score = intent.getIntExtra("score", 0)
        val time = intent.getLongExtra("time", 0L)

        val min = time / 1000L / 60
        val sec = time / 1000L % 60000

        binding.scoreView.text = "10/${score}点"
        if (min == 0L) {
            binding.timeView.text = "${sec}秒"
        }else {
            binding.timeView.text = "${min}分${sec}秒"
        }


        binding.bakcbutton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        return true
    }
}