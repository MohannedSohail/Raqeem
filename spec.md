# المواصفات الفنية التفصيلية (spec.md) — تطبيق "رقيم Raqeem"
## معمارية محرك الذكاء الاصطناعي المحلي (Local Whisper.cpp Architecture)

## 1. الهيكل المعماري والتقني (Technical Architecture)
- **الإطار البرمجي:** Kotlin Multiplatform (KMP) مع Compose Multiplatform (CMP).
- **المنصة الأساسية:** Android (API 26+) مع قابلية العمل المشترك (JVM / iOS / Wasm).
- **محرك تحويل الصوت إلى نص (Speech-to-Text Engine):**
  - **محلي 100% (On-Device):** معمارية قائمة على **Whisper.cpp** ونماذج GGML المضغوطة (Tiny/Base Multilingual) مع ضبط اللغة الافتراضية على العربية (`ar`).
  - **مواصفات الصوت المدعوم:** 16,000 Hz (16 kHz Sample Rate)، Mono (قناة واحدة)، 16-bit PCM WAV.
- **محرك التحليل الدلالي واستخراج المصاريف (Local Dialect NLP Parser):**
  - محلل كوتلن محلي نقي (`commonMain`) فائق السرعة وبدون أي اتصال خارجي.
  - يستخرج بدقة: المبالغ، العملات (SAR, EGP, AED, KWD, USD)، التصنيفات (8 تصنيفات مالية رئيسية)، هوية الدافع (أنا، محمد، عمر...)، والمستفيد.
- **إدارة الحالة:** MVI (Model-View-Intent) بنمط أحادي الاتجاه (UDF) مع StateFlow.
- **التخزين والأمان:** Offline-First بنسبة 100%، حذف الملفات الصوتية المؤقتة فور انتهاء التحويل.

---

## 2. عقود البيانات والواجهات البرمجية (Data & Engine Contracts)

### أ. واجهة محول الصوت المحلي (Local Speech Transcriber Contract)
```kotlin
package org.moh.raqeem.project.ai.whisper

data class WhisperModelConfig(
    val modelName: String = "whisper-tiny-ar",
    val languageCode: String = "ar",
    val threads: Int = 4,
    val isQuantized: Boolean = true
)

data class WhisperTranscribeResult(
    val text: String,
    val language: String = "ar",
    val durationMs: Long,
    val confidence: Float,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

interface LocalSpeechTranscriber {
    suspend fun transcribeAudio(audioFilePath: String, language: String = "ar"): WhisperTranscribeResult
    fun isModelLoaded(): Boolean
    fun getModelName(): String
}
```

### ب. عقد استخراج المصاريف المحلي (Local Dialect Expense Parser)
```kotlin
package org.moh.raqeem.project.ai.nlp

interface LocalExpenseParser {
    fun parseTranscript(transcript: String): ExpenseBatch
}
```

### ج. نماذج بيانات الـ Kotlin (Shared KMP Entities)
```kotlin
@Serializable
data class ExpenseItem(
    val id: String = "",
    val title: String,
    val amount: Double,
    val category: ExpenseCategory,
    val paidBy: String = "أنا",
    val beneficiary: String = "شخصي",
    val dateIso: String,
    val rawVoiceSnippet: String? = null
)

@Serializable
enum class ExpenseCategory(val arabicLabel: String, val iconRes: String) {
    GROCERIES("بقالة وتموينات", "ic_shopping_cart"),
    DINING("مطاعم ومقاهي", "ic_restaurant"),
    TRANSPORTATION("مواصلات وبنزين", "ic_directions_car"),
    HEALTHCARE("صحة وعلاج", "ic_local_hospital"),
    HOME_UTILITIES("فواتير ومنزل", "ic_home"),
    SHOPPING("تسوق وتجزئة", "ic_checkroom"),
    ENTERTAINMENT("ترفيه وتسلية", "ic_sports_esports"),
    OTHER("أخرى", "ic_more_horiz")
}

@Serializable
data class ExpenseBatch(
    val totalAmount: Double,
    val currency: String,
    val confidence: Float,
    val items: List<ExpenseItem>,
    val notes: String? = null,
    val sourceEngine: String = "Whisper.cpp (Local)"
)
```

---

## 3. عقود الصوت للمنصات (16kHz WAV Recorder Contract)

في `shared/src/commonMain`:
```kotlin
expect class AudioRecorder() {
    fun startRecording(outputFilePath: String)
    fun stopRecording(): RecordingResult?
    fun cancelRecording()
    fun isRecording(): Boolean
    fun getAmplitude(): Float
}

data class RecordingResult(
    val filePath: String,
    val durationMs: Long,
    val sampleRate: Int = 16000,
    val channels: Int = 1,
    val mimeType: String = "audio/wav"
)
```

---

## 4. سيناريوهات القبول (Gherkin Scenarios)

### السيناريو 1: تسجيل صوتي محلي بدون اتصال بالإنترنت (Offline Voice Recording)
- **Given** هاتف المستخدم في وضع الطيران (Airplane Mode) ومحرك `Whisper.cpp` جاهز محلياً.
- **When** يضغط المستخدم على زر التسجيل ويقول بالعامية: *"عبيت بنزين 91 بخمسين ريال، وحاسبت قهوة 18 ريال"* ثم ينهي التسجيل.
- **Then** يقوم محرك `LocalWhisperTranscriber` بتحويل الصوت فوراً إلى النص العربي: *"عبيت بنزين 91 بخمسين ريال وحاسبت قهوة 18 ريال"*.
- **And** يقوم `LocalExpenseParser` بتفكيك النص إلى بندين:
  1. بنزين 91 (50 ر.س - مواصلات وبنزين - دافع: أنا).
  2. قهوة (18 ر.س - مطاعم ومقاهي - دافع: أنا).
- **And** تظهر بطاقة المراجعة مع شارة *"معالجة محلية 🔒 Whisper.cpp"* بمجموع 68 ر.س دون إرسال أي بايت للإنترنت.

### السيناريو 2: تقسيم المصاريف المشتركة وتحديد الدافع محلياً
- **Given** المستخدم فتح التطبيق لتسجيل سهرة مشتركة.
- **When** يسجل صوته قائلاً: *"أحمد دفع 120 عشاء، وأنا دفعت 40 حلى"*.
- **Then** يستخرج المحرك المحلي بدقة:
  1. عشاء (120 ر.س - دافع: أحمد - مستفيد: الجميع).
  2. حلى (40 ر.س - دافع: أنا - مستفيد: الجميع).
- **And** ينعكس ذلك فوراً في رادار الحسابات الأسبوعي محلياً.

### السيناريو 3: معالجة نصوص إشعارات البنوك محلياً (Offline Bank SMS)
- **Given** وصل إشعار سحب بنكي (مثال: *"شراء بقيمة 145.50 ر.س من أسواق التميمي بطاقة مدى"*).
- **When** يلصق المستخدم النص في نافذة إشعار البنك ويضغط "تحليل محلي".
- **Then** يحلل المحرك النص محلياً في أقل من 50 مللي ثانية ويستخرج بند بقالة بقيمة 145.50 ر.س.
