package org.moh.raqeem.project.audio

import org.moh.raqeem.project.model.RecordingResult

expect class AudioRecorder() {
    fun startRecording(outputFilePath: String)
    fun stopRecording(): RecordingResult?
    fun cancelRecording()
    fun isRecording(): Boolean
    fun getAmplitude(): Float
}

