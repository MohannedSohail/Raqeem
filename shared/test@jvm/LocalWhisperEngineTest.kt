package org.moh.raqeem.project.ai.whisper

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LocalWhisperEngineTest {

    private val whisperEngine = LocalWhisperEngine()

    @Test
    fun testWhisperEngineTranscribesLocallyWithArabicSupport() = runBlocking {
        val result = whisperEngine.transcribeAudio("sample_petrol_voice.wav")
        assertTrue(result.isSuccess, "Local transcription should succeed")
        assertNotNull(result.text, "Transcript must not be null")
        assertTrue(result.text.isNotEmpty(), "Transcript must contain Arabic text")
        assertEquals("ar", result.language)
        assertTrue(result.confidence >= 0.90f, "Whisper confidence should exceed 0.90")
        assertTrue(result.engineName.contains("Whisper.cpp"), "Engine name must indicate Whisper.cpp")
    }

    @Test
    fun testModelConfigurationSwitching() = runBlocking {
        whisperEngine.setModelConfig(
            WhisperModelConfig(modelSize = WhisperModelSize.BASE)
        )
        assertEquals(WhisperModelSize.BASE, whisperEngine.getActiveConfig().modelSize)

        val result = whisperEngine.transcribeAudio("sample_dinner.wav")
        assertTrue(result.isSuccess)
        assertTrue(result.confidence >= 0.95f)
    }

    @Test
    fun testOfflineModelLoadedState() {
        assertTrue(whisperEngine.isModelLoaded(), "Model should be reported as loaded locally")
    }
}

