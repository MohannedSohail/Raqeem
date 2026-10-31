package org.moh.raqeem.project.model

enum class ExpenseCategory(val arabicLabel: String, val iconEmoji: String) {
    GROCERIES("بقالة وتموينات", "🛒"),
    DINING("مطاعم ومقاهي", "☕"),
    TRANSPORTATION("مواصلات وبنزين", "🚗"),
    HEALTHCARE("صحة وعلاج", "💊"),
    HOME_UTILITIES("فواتير ومنزل", "🏠"),
    SHOPPING("تسوق وتجزئة", "🛍️"),
    ENTERTAINMENT("ترفيه وتسلية", "🎮"),
    OTHER("أخرى", "📦")
}

data class ExpenseItem(
    val id: String,
    val title: String,
    val amount: Double,
    val category: ExpenseCategory,
    val paidBy: String = "أنا",
    val beneficiary: String = "شخصي",
    val dateIso: String,
    val notes: String? = null
)

data class ExpenseBatch(
    val totalAmount: Double,
    val currency: String = "SAR",
    val items: List<ExpenseItem>,
    val notes: String? = null,
    val confidence: Float = 0.95f
)

data class RecordingResult(
    val filePath: String,
    val durationMs: Long,
    val mimeType: String = "audio/mp4"
)

