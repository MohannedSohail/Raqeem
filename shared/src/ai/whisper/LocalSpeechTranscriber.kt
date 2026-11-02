package org.moh.raqeem.project.ai.whisper

interface LocalSpeechTranscriber {
    /**
     * Transcribes an on-device audio file (16kHz mono WAV/PCM) using local Whisper.cpp.
     * Guaranteed to run 100% locally with zero internet network traffic.
     */
    suspend fun transcribeAudio(
        audioFilePath: String,
        config: WhisperModelConfig = WhisperModelConfig()
    ): WhisperTranscribeResult

    /**
     * Checks if the GGML model is loaded in memory and ready for inference.
     */
    fun isModelLoaded(): Boolean

    /**
     * Returns the active model configuration.
     */
    fun getActiveConfig(): WhisperModelConfig

    /**
     * Updates model configuration (e.g. switching between Tiny and Base).
     */
    fun setModelConfig(config: WhisperModelConfig)
}

