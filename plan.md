# خطة التنفيذ التفصيلية (plan.md) — تطبيق "رقيم Raqeem"
## مرحلة الانتقال إلى المحرك المحلي Whisper.cpp (Local Engine Migration)

## مصفوفة مسؤوليات السرب (Swarm Roles)
| الوكيل (Agent) | المسؤولية الرئيسية |
| :--- | :--- |
| **`@ai-specialist`** | تصميم وتنفيذ معمارية `Whisper.cpp` المحلية وإعدادات نماذج GGML وتكامل العربية `ar`. |
| **`@kmp-engineer`** | بناء واجهات KMP النقية (`LocalSpeechTranscriber` و `LocalDialectExpenseParser`) وتكييف مسجل الصوت 16kHz WAV. |
| **`@ux-designer`** | تحديث شاشات Compose لعرض شارة "المحرك المحلي Whisper.cpp 🔒" ونافذة تخصيص النموذج المحلي. |
| **`@db-architect`** | ضمان عمل التخزين المحلي Offline-First وحفظ المصادر المحلية في الكيانات. |
| **`@qa-tester`** | تطبيق منهجية TDD وكتابة Unit Tests لاختبار استخراج النص واللهجات محلياً قبل الكود الفعلي. |
| **`@security-perf`** | التحقق من الخصوصية (عدم وجود أي اتصال بالإنترنت)، منع تسريب الذاكرة، والتأكد من سرعة المعالجة. |

---

## المراحل التنفيذية المتسلسلة (Sequential Phases)

### المرحلة 1: بناء عقود وبيانات المحرك المحلي (AI & Core Contracts)
- [x] توثيق النية في `intent.md` والمواصفات في `spec.md`.
- [ ] إنشاء حزمة `org.moh.raqeem.project.ai.whisper`:
  - `WhisperModelConfig.kt`: إعدادات النموذج المحلي (حجم النموذج، خيوط المعالجة، كود اللغة `ar`).
  - `WhisperTranscribeResult.kt`: نتيجة التحويل المحلي.
  - `LocalSpeechTranscriber.kt`: الواجهة الأساسية لتحويل الصوت محلياً.
  - `LocalWhisperEngine.kt`: التنفيذ المحلي لمحرك Whisper.cpp في KMP مع محاكاة المعالجة العصبية المحلية فائقة السرعة ودعم دمج مكتبات JNI / Native C++.

### المرحلة 2: محرك التحليل الدلالي واللهجات العربية المحلي (Local Dialect NLP)
- [ ] إنشاء `LocalDialectExpenseParser.kt`:
  - تحليل المصطلحات المالية الشائعة: (بنزين، كودو، سوبرماركت، صيدلية، مطعم، فواتير...).
  - كشف العملات العربية (SAR, EGP, AED, KWD, USD).
  - تحديد الدافع والمستفيد بدقة ("محمد دفع"، "أنا دفعت"، "حاسب عني").
  - تقسيم التسجيل الصوتي المتعدد إلى بنود منفصلة.

### المرحلة 3: مسجل الصوت المتوافق مع Whisper.cpp (16kHz WAV Audio)
- [ ] تحديث `AudioRecorder`:
  - تسجيل بصيغة 16kHz Mono 16-bit PCM WAV (المعيار الرسمي لـ Whisper.cpp).
  - ضبط حجم وتوقيت التسجيل بما يحفظ بطارية وذاكرة الجهاز.

### المرحلة 4: اختبارات الـ TDD (Unit Tests First)
- [ ] كتابة `LocalWhisperEngineTest.kt`: اختبار تحويل الصوت المحلي وحالات النجاح والخطأ.
- [ ] كتابة `LocalDialectExpenseParserTest.kt`: اختبار تفكيك اللهجات السعودية، المصرية، والشامية.
- [ ] تحديث اختبارات `ExpenseViewModelTest.kt` لتعمل بالكامل بالوضع المحلي مع التأكد من عدم طلب أي مفاتيح سحابية.

### المرحلة 5: ربط واجهات المستخدم والـ MVI (Compose Multiplatform & MVI)
- [ ] تعديل `ExpenseState` و `ExpenseViewModel`:
  - استبدال التبعية الإلزامية للـ Online API بـ `LocalSpeechTranscriber`.
  - إضافة حالة `localEngineModel` وحالة `isOfflineLocalMode = true`.
- [ ] تحديث شاشة لوحة التحكم `RaqeemDashboardScreen`:
  - إظهار شارة "محرك Whisper.cpp محلي 🔒" أعلى الشاشة لتعزيز ثقة المستخدم.
- [ ] تحديث شاشة الإعدادات `GeminiSettingsDialog` لتصبح `AiEngineSettingsDialog` (اختيار حجم نموذج Whisper المحلي: Tiny / Base، مع إمكانية التبديل الاختياري للسحابة).

### المرحلة 6: المراجعة وتأكيد البناء (Review & Verification)
- [ ] تشغيل اختبارات KMP والتأكد من اجتيازها بنسبة 100%.
- [ ] فحص كود الأندرويد والـ Pure Kotlin لضمان عدم وجود تسريب للذاكرة.
- [ ] توثيق المراجعة في `review.md`.
