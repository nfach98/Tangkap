package com.example.catchball

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log


class MainActivity : AppCompatActivity() {
    // Frame
    private var frameHeight = 0
    private var frameWidth = 0

    private var charRight: Drawable? = null
    private var charLeft: Drawable? = null
    private var charCenter: Drawable? = null

    // Size
    private var charSize = 0

    // Position
    private var charX = 0f
    private var charY = 0f
    private var blackX = 0f
    private var blackY = 0f
    private var orangeX = 0f
    private var orangeY = 0f
    private var pinkX = 0f
    private var pinkY = 0f

    // Score
    private var score = 0
    private var highScore = 0
    private var timeCount = 0
    private var settings: SharedPreferences? = null

    // Class
    private var timer: Timer? = null
    private val handler = Handler()
    private var soundPlayer: SoundPlayer? = null

    // Status
    private var startFlg = false
    private var actionFlg = false
    private var actionLeft = false
    private var actionRight = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        soundPlayer = SoundPlayer(this)

        charLeft = ContextCompat.getDrawable(this, R.drawable.char_left)
        charRight = ContextCompat.getDrawable(this, R.drawable.char_right)
        charCenter = ContextCompat.getDrawable(this, R.drawable.char_center)

        settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE)

        scoreLabel.text = "Score : 0"
        timeLabel.text = "00:10"
    }

    private fun startTimer(){
        timer = Timer(10000)
        timer?.start()
    }

    private fun updateTimer(second: Long){
        if(timer!=null) {
            val millis = timer?.millisUntilFinished?.plus(second)
            timer?.cancel()
            timer = millis?.let { Timer(it) }
            timer?.start()
        }else{
            startTimer()
        }
    }

    fun changePos() { // Add timeCount
        timeCount += 20

        // Orange
        orangeY += (12f + (score/100 * 0.5f))
        val orangeCenterX: Float = orangeX + orange.width / 2
        val orangeCenterY: Float = orangeY + orange.height / 2
        if (hitCheck(orangeCenterX, orangeCenterY)) {
            orangeY = frameHeight + 100.toFloat()
            score += 10
            updateTimer(500L)
            soundPlayer!!.playHitOrangeSound()
        }
        if (orangeY > frameHeight) {
            orangeY = -100f
            orangeX = floor(Math.random() * (frameWidth - orange.width)).toFloat()
        }
        orange.x = orangeX
        orange.y = orangeY

        // Pink
        pinkY += (20f + (score/100 * 0.5f))
        val pinkCenterX = pinkX + pink.width / 2
        val pinkCenterY = pinkY + pink.width / 2
        if (hitCheck(pinkCenterX, pinkCenterY)) {
            pinkY = frameHeight + 30.toFloat()
            score += 30
            updateTimer(3000L)
            soundPlayer?.playHitPinkSound()
        }
        if (pinkY > frameHeight) {
            pinkY = -100f
            pinkX = floor(Math.random() * (frameWidth - orange.width)).toFloat()
        }
        pink.x = pinkX
        pink.y = pinkY

        // Black
        blackY += (18f + (score/100 * 0.5f))
        val blackCenterX: Float = blackX + black.width / 2
        val blackCenterY: Float = blackY + black.height / 2
        if (hitCheck(blackCenterX, blackCenterY)) {
            blackY = frameHeight + 100.toFloat()
            soundPlayer?.playHitBlackSound()
            updateTimer(-5000L)
        }
        if (blackY > frameHeight) {
            blackY = -100f
            blackX = floor(Math.random() * (frameWidth - black.width)).toFloat()
        }
        black.x = blackX
        black.y = blackY

        // Move Box
        if (actionFlg) {
            when{
                actionLeft -> {
                    charX -= 14f
                    character.setImageDrawable(charLeft)
                }
                actionRight -> {
                    charX += 14f
                    character.setImageDrawable(charRight)
                }
            }
        }
        else character.setImageDrawable(charCenter)

        // Check box position.
        if (charX < 0) charX = 0f
        if (frameWidth - charSize < charX) charX = frameWidth - charSize.toFloat()
        character.x = charX
        scoreLabel.text = "Score : $score"
    }

    private fun hitCheck(x: Float, y: Float): Boolean {
        return charX <= x && x <= charX + charSize && charY <= y && y <= frameHeight
    }

    private fun gameOver() { // Stop timer.
        timer?.cancel()
        timer = null
        startFlg = false

        try {
            TimeUnit.SECONDS.sleep(1)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        // Update High Score
        if (score > highScore) {
            val editor = settings?.edit()
            editor?.putInt("HIGH_SCORE", highScore)
            editor?.apply()
        }

        finish()
        val intent = Intent(applicationContext, ResultActivity::class.java)
        intent.putExtra("SCORE", score)
        startActivity(intent)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (startFlg) {
            if (event.action == MotionEvent.ACTION_DOWN) {
                actionFlg = true
                when{
                    event.x < frameWidth/2 -> actionLeft = true
                    event.x >= frameWidth/2 -> actionRight = true
                }
            } else if (event.action == MotionEvent.ACTION_UP) {
                actionFlg = false
                actionLeft = false
                actionRight = false
            }
        }
        else{
            if (event.action == MotionEvent.ACTION_DOWN) startGame()
        }
        return true
    }

    private fun startGame() {
        startLabel.visibility = View.GONE
        startFlg = true
        if (frameHeight == 0) {
            frameHeight = frame.height
            frameWidth = frame.width
            charSize = character.height
            charX = character.x
            charY = character.y
        }

        black.y = 3000.0f
        orange.y = 3000.0f
        pink.y = 3000.0f
        blackY = black.y
        orangeY = orange.y
        pinkY = pink.y

        timeCount = 0
        score = 0
        startTimer()
    }

    inner class Timer(millis: Long) : CountDownTimer(millis, 1){
        var millisUntilFinished: Long = 0
        override fun onFinish() {
            gameOver()
        }

        override fun onTick(millis: Long) {
            millisUntilFinished = millis
            if (startFlg) {
                handler.post { changePos() }
            }
            val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(
                millisUntilFinished - TimeUnit.MILLISECONDS.toSeconds(
                    minutes
                )
            )
            val stringMin = if(minutes < 10) "0$minutes" else "$minutes"
            val stringSec = if(seconds < 10) "0$seconds" else "$seconds"
            timeLabel.text = "$stringMin:$stringSec"
        }
    }
}