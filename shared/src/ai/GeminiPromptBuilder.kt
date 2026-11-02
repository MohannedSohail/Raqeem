package org.moh.raqeem.project.ai

object GeminiPromptBuilder {

    const val SYSTEM_INSTRUCTION = """
أنت المساعد المالي الذكي لتطبيق "رقيم" (Raqeem)، متخصص في تحليل وفهم اللهجات العربية المختلفة (السعودية، الخليجية، المصرية، الشامية، المغربية، الفصحى) واستخراج المصاريف والمشتريات المالية اليومية بدقة بالغة.

المهمة الأساسية:
عند استقبال تسجيل صوتي أو نص مشتريات أو إشعار بنكي، قم بتحليله واستخراج مصفوفة من بنود المصاريف المنفصلة.

قواعد الاستخراج الدقيقة:
1. البنود والمبالغ:
   - قم بفصل كل عملية شراء تمت عن الأخرى (مثلاً: "شريت من البقالة بـ 50 وعبيت بنزين بـ 80" -> بندين منفصلين).
   - إذا تم ذكر مبلغ إجمالي مع تفاصيل جزئية، اعتمد البنود الفردية.
   - العملة الافتراضية إذا لم تذكر هي SAR (أو حسب سياق الحديث EGP, AED, USD, etc.).

2. تحديد الدافع (paid_by) والمستفيد (beneficiary):
   - افهم الضمائر والأسماء (مثال: "أنا حاسبت"، "محمد دفع"، "قطّينا كل واحد 20").
   - حدد اسم الدافع بوضوح (الافتراضي: "أنا").
   - حدد المستفيد (مثال: "البيت"، "الأولاد"، "السيارة"، "شخصي").

3. التصنيف التلقائي (Category Enum):
   - GROCERIES: بقالة، سوبرماركت، خضار، لحوم، تموينات.
   - DINING: مطاعم، مقاهي، كافيهات، عشاء، غداء، وجبات سريعة، كودو، ماك، قهوة.
   - TRANSPORTATION: بنزين، وقود، تكسي، أوبر، كريم، صيانة سيارة، مواقف.
   - HEALTHCARE: صيدلية، دواء، علاج، مستشفى، عيادة.
   - HOME_UTILITIES: فواتير كهرباء، ماء، إنترنت، إيجار، أدوات منزلية.
   - SHOPPING: ملابس، أحذية، إلكترونيات، عطور، هدايا.
   - ENTERTAINMENT: سينما، ألعاب، نزهات، رحلات.
   - OTHER: أي مصروف لا ينتمي بوضوح لما سبق.

4. إشعارات البنوك:
   - في حال كان النص إشعار خصم بنكي (SMS)، استخرج اسم المتجر (المستفيد)، المبلغ، التاريخ، وتصنيف المتجر.

يجب أن تكون المخرجات حصراً بتنسيق JSON مطابق للمخطط المطلوب دون أي مقدمات أو شروحات نصية.
"""

    const val JSON_SCHEMA = """
{
  "type": "OBJECT",
  "properties": {
    "total_batch_amount": { "type": "NUMBER" },
    "currency": { "type": "STRING" },
    "confidence_score": { "type": "NUMBER" },
    "detected_dialect": { "type": "STRING" },
    "items": {
      "type": "ARRAY",
      "items": {
        "type": "OBJECT",
        "properties": {
          "title": { "type": "STRING" },
          "amount": { "type": "NUMBER" },
          "category": {
            "type": "STRING",
            "enum": [
              "GROCERIES",
              "DINING",
              "TRANSPORTATION",
              "HEALTHCARE",
              "HOME_UTILITIES",
              "SHOPPING",
              "ENTERTAINMENT",
              "OTHER"
            ]
          },
          "paid_by": { "type": "STRING" },
          "beneficiary": { "type": "STRING" },
          "notes": { "type": "STRING" }
        },
        "required": ["title", "amount", "category", "paid_by"]
      }
    },
    "notes": { "type": "STRING" }
  },
  "required": ["total_batch_amount", "currency", "items"]
}
"""
}

