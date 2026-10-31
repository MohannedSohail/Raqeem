package org.moh.raqeem.project.audio

import android.media.MediaRecorder
import android.os.Build
import org.moh.raqeem.project.model.RecordingResult
import java.io.File

actual class AudioRecorder {
    private var recorder: MediaRecorder? = null
    private var currentFilePath: String? = null
    private var startTimeMs: Long = 0L
    private var recording: Boolean = false

    actual fun startRecording(outputFilePath: String) {
        try {
            stopRecording() // Clean up any active session safely
            currentFilePath = outputFilePath
            startTimeMs = System.currentTimeMillis()

            val mr = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder()
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mr.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(96000)
                setAudioSamplingRate(44100)
                setOutputFile(outputFilePath)
                prepare()
                start()
            }

            recorder = mr
            recording = true
        } catch (e: Exception) {
            e.printStackTrace()
            releaseRecorder()
        }
    }

    actual fun stopRecording(): RecordingResult? {
        if (!recording || recorder == null) return null

        val path = currentFilePath ?: return null
        val duration = System.currentTimeMillis() - startTimeMs

        return try {
            recorder?.apply {
                stop()
                reset()
                release()
            }
            recorder = null
            recording = false

            val file = File(path)
            if (file.exists() && file.length() > 0) {
                RecordingResult(
                    filePath = path,
                    durationMs = duration,
                    mimeType = "audio/mp4"
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            releaseRecorder()
            null
        }
    }

    actual fun cancelRecording() {
        try {
            recorder?.apply {
                stop()
                reset()
                release()
            }
        } catch (e: Exception) {
            // Ignore cancel exception
        } finally {
            releaseRecorder()
            currentFilePath?.let { File(it).delete() }
            currentFilePath = null
        }
    }

    actual fun isRecording(): Boolean = recording

    actual fun getAmplitude(): Float {
        if (!recording || recorder == null) return 0f
        return try {
            val maxAmp = recorder?.maxAmplitude ?: 0
            (maxAmp.toFloat() / 32767f).coerceIn(0f, 1f)
        } catch (e: Exception) {
            0f
        }
    }

    private fun releaseRecorder() {
        try {
            recorder?.release()
        } catch (e: Exception) {
            // Safe release
        }
        recorder = null
        recording = false
    }
}

