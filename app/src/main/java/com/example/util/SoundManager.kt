package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object SoundManager {
    private const val SAMPLE_RATE = 44100
    private val scope = CoroutineScope(Dispatchers.Default)

    enum class SoundType {
        TAP,
        CORRECT,
        WRONG,
        TICK,
        STREAK,
        LIFELINE,
        HINT,
        VICTORY,
        START,
        // Church & Sacred Sound Sanctuary
        CHURCH_BELL,
        CHURCH_CHOIR,
        CHURCH_CLAPPING,
        CHURCH_BLESSING,
        CHURCH_PRAYER_CHIME
    }

    private val soundBuffers = mutableMapOf<SoundType, ShortArray>()

    init {
        generateAllSounds()
    }

    private fun generateAllSounds() {
        soundBuffers[SoundType.TAP] = generateTapSound()
        soundBuffers[SoundType.CORRECT] = generateCorrectSound()
        soundBuffers[SoundType.WRONG] = generateWrongSound()
        soundBuffers[SoundType.TICK] = generateTickSound()
        soundBuffers[SoundType.STREAK] = generateStreakSound()
        soundBuffers[SoundType.LIFELINE] = generateLifelineSound()
        soundBuffers[SoundType.HINT] = generateHintSound()
        soundBuffers[SoundType.VICTORY] = generateVictorySound()
        soundBuffers[SoundType.START] = generateStartSound()

        // Church sounds
        soundBuffers[SoundType.CHURCH_BELL] = generateChurchBellSound()
        soundBuffers[SoundType.CHURCH_CHOIR] = generateChurchChoirSound()
        soundBuffers[SoundType.CHURCH_CLAPPING] = generateChurchClappingSound()
        soundBuffers[SoundType.CHURCH_BLESSING] = generateChurchBlessingSound()
        soundBuffers[SoundType.CHURCH_PRAYER_CHIME] = generateChurchPrayerChimeSound()
    }

    fun play(sound: SoundType, isEnabled: Boolean) {
        if (!isEnabled) return
        scope.launch {
            try {
                val buffer = soundBuffers[sound] ?: return@launch
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.play()
                val durationMs = (buffer.size * 1000L / SAMPLE_RATE) + 80
                kotlinx.coroutines.delay(durationMs)
                track.stop()
                track.release()
            } catch (_: Exception) {
                // Gracefully handle hardware audio limits
            }
        }
    }

    private fun generateTone(
        frequencies: List<Pair<Double, Double>>,
        volume: Double = 0.85
    ): ShortArray {
        val totalDuration = frequencies.sumOf { it.second }
        val totalSamples = (totalDuration * SAMPLE_RATE).toInt()
        val buffer = ShortArray(totalSamples)

        var sampleOffset = 0
        for ((freq, dur) in frequencies) {
            val noteSamples = (dur * SAMPLE_RATE).toInt()
            for (i in 0 until noteSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val progress = i.toDouble() / noteSamples

                val attackSamples = (0.005 * SAMPLE_RATE).coerceAtLeast(1.0)
                val attack = if (i < attackSamples) i / attackSamples else 1.0
                val decay = exp(-3.5 * progress)

                val fundamental = sin(2.0 * PI * freq * t)
                val harmonic = 0.25 * sin(2.0 * PI * (freq * 2.0) * t)
                val sampleValue = (fundamental + harmonic) * attack * decay * volume

                val index = sampleOffset + i
                if (index < totalSamples) {
                    buffer[index] = (sampleValue * 32767.0).coerceIn(-32767.0, 32767.0).toInt().toShort()
                }
            }
            sampleOffset += noteSamples
        }
        return buffer
    }

    private fun generateTapSound(): ShortArray {
        val samples = (0.025 * SAMPLE_RATE).toInt()
        val buffer = ShortArray(samples)
        for (i in 0 until samples) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-80.0 * t)
            val v = sin(2.0 * PI * 880.0 * t) * env * 0.45
            buffer[i] = (v * 32767.0).coerceIn(-32767.0, 32767.0).toInt().toShort()
        }
        return buffer
    }

    private fun generateCorrectSound(): ShortArray {
        // Melodic celebratory chime with warm church harmony
        return generateTone(
            listOf(
                523.25 to 0.08, // C5
                659.25 to 0.08, // E5
                783.99 to 0.28  // G5
            ),
            volume = 0.82
        )
    }

    private fun generateWrongSound(): ShortArray {
        return generateTone(
            listOf(
                220.0 to 0.12,
                174.61 to 0.22
            ),
            volume = 0.65
        )
    }

    private fun generateTickSound(): ShortArray {
        val samples = (0.015 * SAMPLE_RATE).toInt()
        val buffer = ShortArray(samples)
        for (i in 0 until samples) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-120.0 * t)
            val v = sin(2.0 * PI * 1200.0 * t) * env * 0.35
            buffer[i] = (v * 32767.0).coerceIn(-32767.0, 32767.0).toInt().toShort()
        }
        return buffer
    }

    private fun generateStreakSound(): ShortArray {
        return generateTone(
            listOf(
                783.99 to 0.07,
                987.77 to 0.07,
                1174.66 to 0.07,
                1567.98 to 0.30
            ),
            volume = 0.85
        )
    }

    private fun generateLifelineSound(): ShortArray {
        return generateTone(
            listOf(
                587.33 to 0.10,
                880.00 to 0.25
            ),
            volume = 0.75
        )
    }

    private fun generateHintSound(): ShortArray {
        return generateTone(
            listOf(
                659.25 to 0.12,
                987.77 to 0.28
            ),
            volume = 0.75
        )
    }

    private fun generateVictorySound(): ShortArray {
        return generateTone(
            listOf(
                523.25 to 0.12,
                659.25 to 0.12,
                783.99 to 0.14,
                1046.50 to 0.45
            ),
            volume = 0.90
        )
    }

    private fun generateStartSound(): ShortArray {
        return generateTone(
            listOf(
                523.25 to 0.09,
                783.99 to 0.22
            ),
            volume = 0.75
        )
    }

    // =========================================================================
    // SACRED CHURCH SOUNDS (GUTA RAJEHOVAH TRADITION)
    // =========================================================================

    /**
     * Bhero reKunamata (Holy Sanctuary Church Bell)
     * Synthesizes cast-bronze acoustics with strike tone (C5 523Hz), tierce (Eb5 622Hz),
     * quint (G5 784Hz), nominal (C6 1046Hz), and sub-octave hum (C4 261Hz) with long 1.4s reverberation.
     */
    private fun generateChurchBellSound(): ShortArray {
        val durationSec = 1.4
        val totalSamples = (durationSec * SAMPLE_RATE).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = t / durationSec

            // Sharp bell strike attack (3ms), then exponential acoustic decay
            val attack = if (t < 0.003) (t / 0.003) else 1.0
            val decay = exp(-2.8 * t)

            // Characteristic inharmonic partials of a church bell
            val hum = 0.28 * sin(2.0 * PI * 261.63 * t)
            val prime = 0.42 * sin(2.0 * PI * 523.25 * t)
            val tierce = 0.32 * sin(2.0 * PI * 622.25 * t)
            val quint = 0.26 * sin(2.0 * PI * 783.99 * t)
            val nominal = 0.22 * sin(2.0 * PI * 1046.50 * t)
            val chime = 0.12 * sin(2.0 * PI * 1567.98 * t)

            val sampleVal = (hum + prime + tierce + quint + nominal + chime) * attack * decay * 0.90
            buffer[i] = (sampleVal * 32767.0).coerceIn(-32767.0, 32767.0).toInt().toShort()
        }
        return buffer
    }

    /**
     * Coro A Cappella da Guta (Nziyo dzeGuta - Sacred Choir Harmony)
     * Simulates a four-part sacred vocal choir singing a reverent "Amen / Ngaizviitwe"
     * chord resolution (F major -> C major) with natural vocal formants and subtle vibrato.
     */
    private fun generateChurchChoirSound(): ShortArray {
        val durationSec = 1.6
        val totalSamples = (durationSec * SAMPLE_RATE).toInt()
        val buffer = ShortArray(totalSamples)

        val splitSample = (0.75 * SAMPLE_RATE).toInt()

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val vibrato = sin(2.0 * PI * 5.2 * t) * 1.5 // 5.2Hz human vocal vibrato

            val sampleVal: Double
            if (i < splitSample) {
                // Chord 1: F Major (F3 174.6Hz, A3 220Hz, C4 261.6Hz, F4 349.2Hz)
                val tLocal = t
                val env = if (tLocal < 0.08) (tLocal / 0.08) else 1.0 - (tLocal / 0.75) * 0.25

                val b = 0.28 * sin(2.0 * PI * (174.61 + vibrato) * t)
                val tPart = 0.28 * sin(2.0 * PI * (220.00 + vibrato) * t)
                val a = 0.26 * sin(2.0 * PI * (261.63 + vibrato) * t)
                val s = 0.24 * sin(2.0 * PI * (349.23 + vibrato) * t)
                sampleVal = (b + tPart + a + s) * env
            } else {
                // Chord 2: C Major resolution (C3 130.8Hz, G3 196Hz, C4 261.6Hz, E4 329.6Hz)
                val tLocal = (i - splitSample).toDouble() / SAMPLE_RATE
                val dur2 = durationSec - 0.75
                val env = if (tLocal < 0.06) (tLocal / 0.06) else exp(-2.2 * (tLocal / dur2))

                val b = 0.30 * sin(2.0 * PI * (130.81 + vibrato) * t)
                val tPart = 0.28 * sin(2.0 * PI * (196.00 + vibrato) * t)
                val a = 0.26 * sin(2.0 * PI * (261.63 + vibrato) * t)
                val s = 0.25 * sin(2.0 * PI * (329.63 + vibrato) * t)
                sampleVal = (b + tPart + a + s) * env
            }

            buffer[i] = (sampleVal * 0.85 * 32767.0).coerceIn(-32767.0, 32767.0).toInt().toShort()
        }
        return buffer
    }

    /**
     * Kuuchira (Sacred Worship Clapping)
     * In Guta raJehovah, hand clapping is done with deep reverence, cupped hands, and rhythm.
     * Generates 3 rhythmic acoustic sacred claps at 0.0s, 0.22s, 0.44s.
     */
    private fun generateChurchClappingSound(): ShortArray {
        val durationSec = 0.75
        val totalSamples = (durationSec * SAMPLE_RATE).toInt()
        val buffer = ShortArray(totalSamples)

        val clapInterval = (0.22 * SAMPLE_RATE).toInt()

        for (clap in 0..2) {
            val clapOffset = clap * clapInterval
            val clapDuration = (0.07 * SAMPLE_RATE).toInt()
            for (i in 0 until clapDuration) {
                val t = i.toDouble() / SAMPLE_RATE
                val env = exp(-60.0 * t) // sharp decay of acoustic hand slap
                // Palm hollow body resonance + transient contact
                val palm = sin(2.0 * PI * 420.0 * t) * 0.55
                val snap = sin(2.0 * PI * 1150.0 * t) * 0.35
                val air = sin(2.0 * PI * 2200.0 * t) * 0.20
                val v = (palm + snap + air) * env * 0.80

                val idx = clapOffset + i
                if (idx < totalSamples) {
                    val existing = buffer[idx].toDouble() / 32767.0
                    val combined = (existing + v).coerceIn(-1.0, 1.0)
                    buffer[idx] = (combined * 32767.0).toInt().toShort()
                }
            }
        }
        return buffer
    }

    /**
     * Harmonia de Bênção Sagrada (Ngaizviitwe / Peace & Blessing)
     */
    private fun generateChurchBlessingSound(): ShortArray {
        return generateTone(
            listOf(
                392.00 to 0.25, // G4
                523.25 to 0.25, // C5
                659.25 to 0.40, // E5
                523.25 to 0.50  // C5 (soft resolution)
            ),
            volume = 0.75
        )
    }

    /**
     * Sino Suave de Oração e Meditação (Sanctuary Prayer Chime)
     */
    private fun generateChurchPrayerChimeSound(): ShortArray {
        val durationSec = 1.1
        val totalSamples = (durationSec * SAMPLE_RATE).toInt()
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val attack = if (t < 0.004) t / 0.004 else 1.0
            val decay = exp(-3.2 * t)

            // High silver prayer chime
            val tone1 = 0.50 * sin(2.0 * PI * 659.25 * t) // E5
            val tone2 = 0.35 * sin(2.0 * PI * 1318.51 * t) // E6
            val tone3 = 0.25 * sin(2.0 * PI * 1975.53 * t) // B6

            val sampleVal = (tone1 + tone2 + tone3) * attack * decay * 0.85
            buffer[i] = (sampleVal * 32767.0).coerceIn(-32767.0, 32767.0).toInt().toShort()
        }
        return buffer
    }
}
