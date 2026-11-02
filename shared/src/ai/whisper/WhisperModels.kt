package org.moh.raqeem.project.ai.whisper

import kotlinx.serialization.Serializable

@Serializable
enum class WhisperModelSize(
    val labelArabic: String,
    val modelFileName: String,
    val approximateSizeMb: Int
) {
    TINY("نموذج فائق السرعة (Tiny - 75MB)", "ggml-tiny-ar.bin", 75),
    BASE("نموذج متوازن عالي الدقة (Base - 142MB)", "ggml-base-ar.bin", 142),
    SMALL("نموذج دقيق متقدم (Small - 465MB)", "ggml-small-ar.bin", 465)
}

@Serializable
data class WhisperModelConfig(
    val modelSize: WhisperModelSize = WhisperModelSize.TINY,
    val languageCode: String = "ar",
    val threads: Int = 4,
    val isQuantized: Boolean = true,
    val isTranslateToEnglish: Boolean = false
)

@Serializable
data class WhisperTranscribeResult(
    val text: String,
    val language: String = "ar",
    val durationMs: Long = 0L,
    val confidence: Float = 0.95f,
    val isSuccess: Boolean = true,
    val errorMessage: String? = null,
    val engineName: String = "Whisper.cpp (Local Embedded)"
)

