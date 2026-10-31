# المواصفات الفنية التفصيلية (spec.md) — تطبيق "رقيم Raqeem"

## 1. الهيكل المعماري والتقني (Technical Architecture)
- **الإطار البرمجي:** Kotlin Multiplatform (KMP) مع Compose Multiplatform (CMP).
- **المنصة المستهدفة الأولى:** Android (API 26+ / Android 8.0+).
- **نمط التصميم (Architecture Pattern):** MVI (Model-View-Intent) مع StateFlow للـ Unidirectional Data Flow (UDF).
- **نظام التصميم:** Material Design 3، مخصص بالكامل لـ RTL وخطوط عربية حديثة.
- **نموذج الذكاء الاصطناعي:** **Google Gemini 2.5 Flash** مع ميزة المخرجات المهيكلة (`application/json`).
- **التخزين المحلي:** قاعدة بيانات محلية Offline-First (Room KMP / SQLite) مع `kotlinx.serialization`.

---

## 2. عقود البيانات ومخططات الـ JSON (Data Contracts)

### أ. مخطط استخراج المصاريف من الصوت (Gemini Structured Output Schema)
```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "ExpenseExtractionResult",
  "type": "object",
  "properties": {
    "total_batch_amount": {
      "type": "number",
      "description": "إجمالي المبالغ المذكورة في التسجيل"
    },
    "currency": {
      "type": "string",
      "description": "رمز العملة (مثال: SAR, EGP, AED, USD). الافتراضي حسب سياق المستخدم"
    },
    "confidence_score": {
      "type": "number",
      "minimum": 0.0,
      "maximum": 1.0,
      "description": "نسبة وضوح وثقة النموذج في الصوت والبنود"
    },
    "detected_dialect": {
      "type": "string",
      "description": "اللهجة المكتشفة (مثال: Saudi, Egyptian, Levantine, Modern Standard Arabic)"
    },
    "items": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "id": { "type": "string" },
          "title": { "type": "string", "description": "اسم البند أو الخدمة التي تم شراؤها" },
          "amount": { "type": "number", "description": "قيمة المصروف" },
          "category": {
            "type": "string",
            "enum": [
              "GROCERIES",       // تموينات وبقالة
              "DINING",          // مطاعم وكافيهات
              "TRANSPORTATION",   // مواصلات وبنزين وتكسي
              "HEALTHCARE",      // صيدلية وعلاج
              "HOME_UTILITIES",  // فواتير وأغراض منزل
              "SHOPPING",        // ملابس وإلكترونيات
              "ENTERTAINMENT",   // ترفيه ونزهات
              "OTHER"            // أخرى
            ]
          },
          "paid_by": {
            "type": "string",
            "description": "اسم الشخص الذي دفع (مثال: 'أنا'، 'محمد'، 'أحمد')"
          },
          "beneficiary": {
            "type": "string",
            "description": "الجهة المستفيدة (مثال: 'البيت'، 'الجميع'، 'شخصي')"
          },
          "date": {
            "type": "string",
            "format": "date",
            "description": "تاريخ العملية بصيغة YYYY-MM-DD"
          }
        },
        "required": ["title", "amount", "category", "paid_by"]
      }
    },
    "notes": {
      "type": "string",
      "description": "أي تفاصيل أو ملاحظات إضافية تم ذكرها شفهياً"
    }
  },
  "required": ["total_batch_amount", "currency", "items"]
}
```

### ب. نماذج بيانات الـ Kotlin (Shared KMP Entities)
```kotlin
@Serializable
data class ExpenseItem(
    val id: String = Uuid.random().toString(),
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
    val notes: String? = null
)
```

---

## 3. عقود الصوت للمنصات (Platform expect/actual Contracts)

في `shared/src/commonMain`:
```kotlin
expect class AudioRecorder() {
    fun startRecording(outputFilePath: String)
    fun stopRecording(): RecordingResult
    fun isRecording(): Boolean
    fun getAmplitudeFlow(): Flow<Float> // لتغذية شكل الموجة في واجهة المستخدم
}

data class RecordingResult(
    val filePath: String,
    val durationMs: Long,
    val mimeType: String
)
```

في `shared/src@android`:
- تنفيذ `AudioRecorder` عبر `android.media.MediaRecorder` أو `AudioRecord` بصيغة AAC / M4A لتقليل حجم الملف المرفوع وزيادة سرعة الإرسال.

---

## 4. سيناريوهات القبول (Gherkin Scenarios)

### السيناريو 1: تسجيل صوتي يحتوي على أكثر من بند وشخصين (P1)
- **Given** المستخدم فتح شاشة رقيم الرئيسية وميزانية اليوم معروضة.
- **When** يضغط المستخدم على زر الميكروفون ويقول: *"حاسبت 70 ريال سوبرماركت، وعمر دفع 30 ريال عصير"* ثم يرفع إصبعه.
- **Then** يتم رفع الملف الصوتي إلى Gemini 2.5 Flash وتظهر بطاقة مراجعة تحتوي على بندين:
  1. سوبرماركت (70 ر.س - بقالة - دافع: أنا).
  2. عصير (30 ر.س - مطاعم - دافع: عمر).
- **And** عند الضغط على "تأكيد وحفظ"، يزداد إجمالي مصاريف اليوم بمقدار 100 ر.س فوراً.

### السيناريو 2: تسجيل صوتي غير واضح أو تشويش عالي
- **Given** المستخدم في بيئة مليئة بالضوضاء.
- **When** يسجل صوتاً غير مفهوم.
- **Then** يعيد الـ API درجة ثقة أقل من 0.4.
- **And** يظهر التطبيق إشعاراً لطيفاً: *"لم نستطع تمييز المبالغ بدقة، هل تود إعادة التسجيل أو كتابتها يدوياً؟"*.

