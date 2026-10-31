# AGENTS.md — مشروع "رقيم Raqeem"

## 1. نظرة عامة على المشروع (Project Vision)
**رقيم (Raqeem)** هو تطبيق تتبع مصاريف يومية وأسبوعية ذكي وموجّه بالعرب أولاً (Arabic-first & Voice-first). يعتمد على التسجيل الصوتي العفوي باللهجات العربية المختلفة (أو إرفاق إشعارات البنوك) لاستخراج وتصنيف المصروفات، وتحديد من دفع ولمن، مع عرض لوحة تحكم فورية ورادار أسبوعي، مبني باستخدام **Compose Multiplatform (CMP / KMP)** مع التركيز الأولي على منصة **Android**.

---

## 2. سلسلة عمل الـ AI SDLC (Spec Kit Methodology)
كل تعديل أو ميزة جديدة يجب أن تتبع التسلسل الصارم التالي:
```text
AGENTS.md ──► intent.md ──► spec.md ──► plan.md ──► tests ──► code ──► review
```
1. **`intent.md`**: توثيق نية الميزة وقيمتها للمستخدم ومؤشرات نجاحها.
2. **`spec.md`**: المواصفات الفنية التفصيلية، عقود البيانات (Data Contracts/JSON Schemas)، وسيناريوهات القبول (Gherkin: Given-When-Then).
3. **`plan.md`**: خطة التقسيم التقني والمهام التتابعية.
4. **`tests` (TDD)**: كتابة وتجهيز الاختبارات أولاً (الـ Unit Tests وفحص نماذج البيانات) قبل الكود الفعلي.
5. **`code`**: تنفيذ كود Kotlin Multiplatform النظيف المطابق للمواصفات.
6. **`review`**: مراجعة شاملة للأداء (Performance)، تسريب الذاكرة (Memory Leaks)، ودعم اللغات وRTL.

---

## 3. سرب الوكلاء المتخصصين (Multi-Agent Swarm Roles)

يتم توزيع المسؤوليات بين الوكلاء المتخصصين في Antigravity كالتالي:

| الوكيل (Agent) | الدور والاختصاص | الملفات والمسؤوليات الأساسية |
| :--- | :--- | :--- |
| **`@ux-designer`** | تصميم واجهات Compose Multiplatform العربية | • تطبيق مهارة `ui-ux-pro-max` وتصميم RTL.<br>• مكونات Material 3، مؤشرات الصوت (Waveform Animation)، Dark/Light Mode.<br>• إدارة الخطوط العربية (Cairo / IBM Plex Sans Arabic). |
| **`@kmp-engineer`** | معمارية الكود المشترك ومنطق التطبيق | • معمارية MVI (State, Intent, Effect).<br>• تعريف واجهات `expect/actual` لتسجيل الصوت بين Android والمنصات الأخرى.<br>• ربط الموديولات وإدارة الـ Toolchain (Amper / Kotlin Multiplatform). |
| **`@ai-specialist`** | تكامل الذكاء الاصطناعي وهندسة الأوامر | • تكامل **Gemini 2.5 Flash** (Audio-to-JSON & Vision/OCR).<br>• ضبط برومبت فهم اللهجات العربية والمصطلحات المالية.<br>• تدقيق صيغة المخرجات المهيكلة (`Structured Output Schema`). |
| **`@db-architect`** | إدارة البيانات والتخزين المحلي | • قاعدة البيانات المحلية (Offline-First).<br>• نماذج الكيانات `ExpenseEntity` و `PayerEntity` و `BatchTransaction`.<br>• المزامنة وإحصائيات المصاريف اليومية والأسبوعية. |
| **`@qa-tester`** | ضمان الجودة وأتمتة الاختبارات (TDD) | • تطبيق مهارة `tdd-orchestrator`.<br>• اختبارات الـ Unit Tests لمنطق التقسيم وتحويل الـ JSON.<br>• Mocking استجابات Gemini والصوتيات. |
| **`@security-perf`** | الأمان، الأداء، وإدارة الذاكرة | • منع تسريب موارد الميكروفون والذاكرة (`AudioRecord` cleanup).<br>• حماية مفاتيح الـ API Tokens وSecure Storage.<br>• إعدادات Proguard وقواعد الحماية وتدقيق WCAG. |

---

## 4. القواعد المعمارية الصارمة (Architectural Guardrails)
1. **عزل المنصات في KMP:**
   - كود `shared/src` يجب أن يظل **Pure Kotlin** خالياً من أي استدعاءات لمكتبات `android.*` إلا عبر نمط `expect / actual`.
2. **عقود الذكاء الاصطناعي:**
   - استدعاء نموذج Gemini يجب أن يعيد حصراً JSON مطابق لـ `ExpenseBatchSchema` مع تفعيل التحقق الصارم `kotlinx.serialization`.
3. **الدعم الكامل للغة العربية و RTL:**
   - جميع شاشات الـ Compose تدعم اتجاه `LayoutDirection.Rtl` بشكل افتراضي، مع احترام المسافات والبادئات في الأرقام والعملات.
4. **أمان البيانات والخصوصية:**
   - التسجيلات الصوتية مؤقتة ويتم حذفها من التخزين المحلي المؤقت فور الانتهاء من معالجتها.

