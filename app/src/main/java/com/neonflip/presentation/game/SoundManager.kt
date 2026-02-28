package com.neonflip.presentation.game

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.neonflip.R

/**
 * A utility class to play custom synthesized retro sound effects
 * using the Android SoundPool API.
 */
class SoundManager(context: Context) {
    private var soundPool: SoundPool? = null
    
    private var jumpSoundId: Int = 0
    private var scoreSoundId: Int = 0
    private var gameOverSoundId: Int = 0
    
    // Track if sounds are loaded
    private var isLoaded = false

    init {
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build()

            // Load sounds into memory
            soundPool?.setOnLoadCompleteListener { _, _, _ ->
                isLoaded = true
            }
            
            jumpSoundId = soundPool?.load(context, R.raw.jump, 1) ?: 0
            scoreSoundId = soundPool?.load(context, R.raw.score, 1) ?: 0
            gameOverSoundId = soundPool?.load(context, R.raw.gameover, 1) ?: 0
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Plays a quick, synth "blip" for jumping.
     */
    fun playJumpSound() {
        if (isLoaded) {
            soundPool?.play(jumpSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    /**
     * Plays a happy sequence of synth notes for scoring a point.
     */
    fun playScoreSound() {
        if (isLoaded) {
            soundPool?.play(scoreSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    /**
     * Plays a harsher, glitchy sliding synth for crashing/game over.
     */
    fun playGameOverSound() {
        if (isLoaded) {
            soundPool?.play(gameOverSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    /**
     * Frees up resources when the sound manager is no longer needed.
     */
    fun release() {
        soundPool?.release()
        soundPool = null
        isLoaded = false
    }
}
