package org.moh.raqeem.project.audio

import org.moh.raqeem.project.model.RecordingResult

actual class AudioRecorder {
    private var recording = false
    private var currentFilePath: String = "stub_wasm.mp4"

    actual fun startRecording(outputFilePath: String) {
        currentFilePath = outputFilePath
        recording = true
    }

    actual fun stopRecording(): RecordingResult? {
        if (!recording) return null
        recording = false
        return RecordingResult(filePath = currentFilePath, durationMs = 2000L)
    }

    actual fun cancelRecording() {
        recording = false
    }

    actual fun isRecording(): Boolean = recording

    actual fun getAmplitude(): Float = if (recording) 0.5f else 0f
}


