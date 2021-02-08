package com.example.catchball

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build

class SoundPlayer(context: Context?) {
    private var audioAttributes: AudioAttributes? = null
    private val SOUND_POOL_MAX = 3

    private var soundPool: SoundPool? = null
    private var hitOrangeSound = 0
    private var hitPinkSound = 0
    private var hitBlackSound = 0

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            soundPool = SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(SOUND_POOL_MAX)
                .build()
        } else {
            soundPool = SoundPool(SOUND_POOL_MAX, AudioManager.STREAM_MUSIC, 0)
        }
        hitOrangeSound = soundPool!!.load(context, R.raw.orange, 1)
        hitPinkSound = soundPool!!.load(context, R.raw.pink, 1)
        hitBlackSound = soundPool!!.load(context, R.raw.black, 1)
    }

    fun playHitOrangeSound() {
        soundPool!!.play(hitOrangeSound, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    fun playHitPinkSound() {
        soundPool!!.play(hitPinkSound, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    fun playHitBlackSound() {
        soundPool!!.play(hitBlackSound, 1.0f, 1.0f, 1, 0, 1.0f)
    }
}