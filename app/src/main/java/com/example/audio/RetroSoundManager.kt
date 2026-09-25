package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class RetroSoundManager {
    var isSoundEnabled: Boolean = true
    private val scope = CoroutineScope(Dispatchers.Default)

    fun playStep() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(frequency = 420.0, durationMs = 35, waveType = WaveType.SQUARE, volume = 0.25f)
        }
    }

    fun playPush() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(frequency = 140.0, durationMs = 70, waveType = WaveType.SQUARE, volume = 0.4f)
        }
    }

    fun playKeyPickup() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(523.25, 45, WaveType.SINE, 0.4f)
            playTone(659.25, 45, WaveType.SINE, 0.4f)
            playTone(783.99, 80, WaveType.SINE, 0.5f)
        }
    }

    fun playDoorUnlock() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(320.0, 50, WaveType.TRIANGLE, 0.4f)
            playTone(240.0, 60, WaveType.TRIANGLE, 0.4f)
            playTone(180.0, 90, WaveType.SQUARE, 0.35f)
        }
    }

    fun playChestOpen() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(440.0, 50, WaveType.SINE, 0.35f)
            playTone(554.37, 50, WaveType.SINE, 0.4f)
            playTone(659.25, 50, WaveType.SINE, 0.45f)
            playTone(880.0, 150, WaveType.SINE, 0.5f)
        }
    }

    fun playPortalWarp() {
        if (!isSoundEnabled) return
        scope.launch {
            for (freq in listOf(220, 330, 440, 660, 880)) {
                playTone(freq.toDouble(), 30, WaveType.SINE, 0.35f)
            }
        }
    }

    fun playIceSlide() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(700.0, 40, WaveType.TRIANGLE, 0.3f)
            playTone(850.0, 50, WaveType.TRIANGLE, 0.3f)
        }
    }

    fun playMirrorRotate() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(580.0, 40, WaveType.SQUARE, 0.35f)
        }
    }

    fun playWinFanfare() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(523.25, 80, WaveType.SINE, 0.4f) // C5
            playTone(659.25, 80, WaveType.SINE, 0.4f) // E5
            playTone(783.99, 80, WaveType.SINE, 0.45f) // G5
            playTone(1046.50, 240, WaveType.SINE, 0.55f) // C6
        }
    }

    fun playDefeat() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(330.0, 80, WaveType.SQUARE, 0.4f)
            playTone(280.0, 90, WaveType.SQUARE, 0.4f)
            playTone(220.0, 140, WaveType.SQUARE, 0.45f)
        }
    }

    fun playMenuClick() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(880.0, 25, WaveType.SINE, 0.25f)
        }
    }

    fun playUndo() {
        if (!isSoundEnabled) return
        scope.launch {
            playTone(380.0, 30, WaveType.TRIANGLE, 0.3f)
            playTone(300.0, 40, WaveType.TRIANGLE, 0.3f)
        }
    }

    private enum class WaveType { SINE, SQUARE, TRIANGLE }

    private fun playTone(frequency: Double, durationMs: Int, waveType: WaveType, volume: Float) {
        val sampleRate = 22050
        val numSamples = (sampleRate * durationMs) / 1000
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val time = i.toDouble() / sampleRate
            val angle = 2.0 * Math.PI * frequency * time

            // Apply quick envelope attack & release to prevent audio clicking
            val attackSamples = (sampleRate * 0.005).toInt()
            val releaseSamples = (sampleRate * 0.01).toInt()
            val envelope = when {
                i < attackSamples -> i.toFloat() / attackSamples
                i > numSamples - releaseSamples -> (numSamples - i).toFloat() / releaseSamples
                else -> 1.0f
            }

            val rawSample = when (waveType) {
                WaveType.SINE -> sin(angle)
                WaveType.SQUARE -> if (sin(angle) >= 0) 0.7 else -0.7
                WaveType.TRIANGLE -> (2.0 / Math.PI) * Math.asin(sin(angle))
            }

            buffer[i] = (rawSample * Short.MAX_VALUE * volume * envelope).toInt().toShort()
        }

        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()

            // Sleep current coroutine thread until playback completes, then release
            Thread.sleep(durationMs.toLong() + 10)
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) {
            // Audio hardware fallback gracefully
        }
    }
}
