package com.example.catchball

import android.R.attr.start
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        val score = intent.getIntExtra("SCORE", 0)
        scoreLabel.text = score.toString() + ""
        val settings = getSharedPreferences("HIGH_SCORE", Context.MODE_PRIVATE)
        val highScore = settings.getInt("HIGH_SCORE", 0)
        if (score > highScore) {
            highScoreLabel.text = "High Score : $score"
            // Update High Score
            val editor = settings.edit()
            editor.putInt("HIGH_SCORE", score)
            editor.apply()
        } else {
            highScoreLabel.text = "High Score : $highScore"
        }
    }


    fun tryAgain(view: View?) {
        startActivity(Intent(applicationContext, MainActivity::class.java))
    }


    // Disable Return Button
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_BACK -> return true
            }
        }
        return super.dispatchKeyEvent(event)
    }
}
