package org.moh.raqeem.project.model

enum class ExpenseCategory(val arabicLabel: String, val categoryKey: String) {
    GROCERIES("بقالة وتموينات", "groceries"),
    DINING("مطاعم ومقاهي", "dining"),
    TRANSPORTATION("مواصلات وبنزين", "transportation"),
    HEALTHCARE("صحة وعلاج", "healthcare"),
    HOME_UTILITIES("فواتير ومنزل", "utilities"),
    SHOPPING("تسوق وتجزئة", "shopping"),
    ENTERTAINMENT("ترفيه وتسلية", "entertainment"),
    OTHER("أخرى", "other")
}

data class ExpenseItem(
    val id: String,
    val title: String,
    val amount: Double,
    val category: ExpenseCategory,
    val paidBy: String = "أنا",
    val beneficiary: String = "شخصي",
    val dateIso: String,
    val notes: String? = null,
    val rawVoiceSnippet: String? = null
)

data class ExpenseBatch(
    val totalAmount: Double,
    val currency: String = "شيكل",
    val items: List<ExpenseItem>,
    val notes: String? = null,
    val confidence: Float = 0.95f,
    val sourceEngine: String = "Whisper.cpp (Local Offline)"
)

data class RecordingResult(
    val filePath: String,
    val durationMs: Long,
    val mimeType: String = "audio/wav"
)
