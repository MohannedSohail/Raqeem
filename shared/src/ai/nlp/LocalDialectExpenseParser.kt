package org.moh.raqeem.project.ai.nlp

import org.moh.raqeem.project.model.ExpenseBatch
import org.moh.raqeem.project.model.ExpenseCategory
import org.moh.raqeem.project.model.ExpenseItem

/**
 * High-performance on-device Arabic dialect and financial entity extractor.
 * Defaults to Shekel (شيكل) as the primary currency.
 * Operates strictly locally in pure Kotlin (commonMain) with zero network dependencies.
 */
class LocalDialectExpenseParser {

    fun parseTranscript(transcript: String): ExpenseBatch {
        val cleanText = transcript.trim()
        if (cleanText.isEmpty()) {
            return ExpenseBatch(
                totalAmount = 0.0,
                currency = "شيكل",
                confidence = 0f,
                items = emptyList(),
                sourceEngine = "Whisper.cpp (Local Offline)"
            )
        }

        val detectedCurrency = detectCurrency(cleanText)
        val items = mutableListOf<ExpenseItem>()
        var idCounter = 1

        // Split transcript into distinct expense segments based on punctuation and currency markers
        val segments = splitIntoExpenseSegments(cleanText)

        for (segment in segments) {
            val trimmed = segment.trim()
            if (trimmed.length < 3) continue

            val amount = extractAmount(trimmed)
            val category = detectCategory(trimmed)

            // If no amount found and not clearly an expense keyword, skip fragment
            if (amount <= 0.0 && !containsExpenseKeywords(trimmed)) {
                continue
            }

            val finalAmount = if (amount > 0.0) amount else 20.0
            val title = detectTitle(trimmed, category)
            val paidBy = detectPayer(trimmed)
            val beneficiary = detectBeneficiary(trimmed)

            items.add(
                ExpenseItem(
                    id = "local_exp_${idCounter++}",
                    title = title,
                    amount = finalAmount,
                    category = category,
                    paidBy = paidBy,
                    beneficiary = beneficiary,
                    dateIso = "2026-10-31",
                    rawVoiceSnippet = trimmed
                )
            )
        }

        // If splitting didn't yield items, fallback to single item from full text
        if (items.isEmpty()) {
            val amount = extractAmount(cleanText)
            val category = detectCategory(cleanText)
            items.add(
                ExpenseItem(
                    id = "local_exp_1",
                    title = detectTitle(cleanText, category),
                    amount = if (amount > 0.0) amount else 50.0,
                    category = category,
                    paidBy = detectPayer(cleanText),
                    beneficiary = detectBeneficiary(cleanText),
                    dateIso = "2026-10-31",
                    rawVoiceSnippet = cleanText
                )
            )
        }

        val total = items.sumOf { it.amount }

        return ExpenseBatch(
            totalAmount = total,
            currency = detectedCurrency,
            confidence = 0.96f,
            items = items,
            notes = "معالجة محلية بالكامل عبر Whisper.cpp (بدون إنترنت)",
            sourceEngine = "Whisper.cpp (Local Offline)"
        )
    }

    private fun detectCurrency(text: String): String {
        return when {
            text.contains("شيكل") || text.contains("شواكل") || text.contains("شقل") ||
            text.contains("شاقل") || text.contains("ILS") || text.contains("NIS") || text.contains("₪") -> "شيكل"
            text.contains("جنيه") || text.contains("مصري") -> "EGP"
            text.contains("درهم") || text.contains("إماراتي") -> "AED"
            text.contains("دينار") || text.contains("كويتي") || text.contains("أردني") -> "KWD"
            text.contains("دولار") || text.contains("USD") -> "USD"
            text.contains("ريال") || text.contains("سعودي") -> "SAR"
            text.contains("يورو") -> "EUR"
            else -> "شيكل" // Default currency is Shekel!
        }
    }

    private fun splitIntoExpenseSegments(text: String): List<String> {
        // Protect compound nouns before splitting
        val sanitized = text
            .replace("،", ",")
            .replace("بقالة وتموينات", "بقالة_وتموينات")
            .replace("خضار وفواكه", "خضار_وفواكه")
            .replace("قهوة ومشروبات", "قهوة_ومشروبات")
            .replace("قهوة وحلى", "قهوة_وحلى")
            .replace("عشاء وغداء", "عشاء_وغداء")
            .replace("فواتير ومنزل", "فواتير_ومنزل")

        // Split on commas, or on 'و' when followed by another person/item
        val pattern = Regex("[,]|(;)|(\\s+ثم\\s+)|(\\s+بعدين\\s+)|(\\s+و(?=(?:محمد|أحمد|عمر|خالد|سارة|عبيت|حاسبت|دفعت|\\d+|بـ|ب|شريت)))")
        val rawParts = sanitized.split(pattern).filter { it.isNotBlank() }

        // Post-process: restore protected spaces
        val restoredParts = rawParts.map { part ->
            part.replace("_", " ").trim()
        }

        // Merge orphan payer fragments back to previous segment
        val merged = mutableListOf<String>()
        for (part in restoredParts) {
            val isJustPayer = (part.startsWith("وأحمد") || part.startsWith("أحمد دفع") ||
                               part.startsWith("ومحمد") || part.startsWith("محمد حاسب")) &&
                               extractAmount(part) == 0.0
            if (isJustPayer && merged.isNotEmpty()) {
                val lastIdx = merged.size - 1
                merged[lastIdx] = merged[lastIdx] + " " + part
            } else {
                merged.add(part)
            }
        }

        return merged
    }

    private fun containsExpenseKeywords(text: String): Boolean {
        val keywords = listOf(
            "بنزين", "قهوة", "مطعم", "غداء", "عشاء", "سوبرماركت", "بقالة", "تموينات",
            "صيدلية", "دواء", "فاتورة", "شراء", "حاسبت", "دفعت", "شيكل", "شواكل", "شقل", "ريال", "جنيه"
        )
        return keywords.any { text.contains(it) }
    }

    private fun detectCategory(text: String): ExpenseCategory {
        return when {
            text.contains("بنزين") || text.contains("وقود") || text.contains("تكسي") ||
            text.contains("أوبر") || text.contains("كريم") || text.contains("مواقف") ||
            text.contains("غسيل سيارة") || text.contains("زيت") -> ExpenseCategory.TRANSPORTATION

            text.contains("قهوة") || text.contains("كافيه") || text.contains("عشاء") ||
            text.contains("غداء") || text.contains("مطعم") || text.contains("كودو") ||
            text.contains("ماك") || text.contains("بيك") || text.contains("عصير") ||
            text.contains("حلى") || text.contains("شاورما") || text.contains("فطور") -> ExpenseCategory.DINING

            text.contains("سوبرماركت") || text.contains("بقالة") || text.contains("تموينات") ||
            text.contains("بنده") || text.contains("خضار") || text.contains("حليب") || text.contains("خبز") ||
            text.contains("دكان") -> ExpenseCategory.GROCERIES

            text.contains("صيدلية") || text.contains("دواء") || text.contains("علاج") ||
            text.contains("بندول") || text.contains("مستشفى") || text.contains("طبيب") ||
            text.contains("فيتامين") -> ExpenseCategory.HEALTHCARE

            text.contains("فاتورة") || text.contains("كهرباء") || text.contains("مياه") ||
            text.contains("إنترنت") || text.contains("إيجار") || text.contains("صيانة") ||
            text.contains("أغراض منزل") -> ExpenseCategory.HOME_UTILITIES

            text.contains("سوق") || text.contains("ملابس") || text.contains("ثوب") ||
            text.contains("حذاء") || text.contains("أمازون") || text.contains("نون") ||
            text.contains("إلكترونيات") || text.contains("عطور") -> ExpenseCategory.SHOPPING

            text.contains("سينما") || text.contains("ملاهي") || text.contains("ألعاب") ||
            text.contains("بولينغ") || text.contains("تذكرة") -> ExpenseCategory.ENTERTAINMENT

            else -> ExpenseCategory.OTHER
        }
    }

    private fun detectTitle(text: String, category: ExpenseCategory): String {
        return when (category) {
            ExpenseCategory.TRANSPORTATION -> {
                if (text.contains("بنزين 91")) "بنزين 91"
                else if (text.contains("بنزين 95")) "بنزين 95"
                else if (text.contains("بنزين")) "بنزين ووقود"
                else if (text.contains("أوبر") || text.contains("تكسي")) "مشوار تكسي"
                else "مواصلات ونقل"
            }
            ExpenseCategory.DINING -> {
                if (text.contains("قهوة")) "قهوة ومشروبات"
                else if (text.contains("عشاء")) "عشاء مطعم"
                else if (text.contains("غداء")) "غداء عمل"
                else if (text.contains("فطور")) "فطور صباحي"
                else if (text.contains("حلى")) "حلى ومخبوزات"
                else "وجبة مطعم"
            }
            ExpenseCategory.GROCERIES -> {
                if (text.contains("سوبرماركت")) "أغراض سوبرماركت"
                else if (text.contains("خضار")) "خضار وفواكه"
                else "بقالة وتموينات"
            }
            ExpenseCategory.HEALTHCARE -> {
                if (text.contains("علاج") || text.contains("بندول")) "أدوية وعلاج"
                else "مشتريات صيدلية"
            }
            ExpenseCategory.HOME_UTILITIES -> "فواتير ومنزل"
            ExpenseCategory.SHOPPING -> "تسوق ومشتريات"
            ExpenseCategory.ENTERTAINMENT -> "ترفيه وسياحة"
            ExpenseCategory.OTHER -> "مصروفات عامة"
        }
    }

    private fun detectPayer(text: String): String {
        return when {
            text.contains("محمد دفع") || text.contains("محمد حاسب") || text.contains("محمد دفعها") -> "محمد"
            text.contains("أحمد دفع") || text.contains("أحمد حاسب") -> "أحمد"
            text.contains("عمر دفع") || text.contains("عمر حاسب") -> "عمر"
            text.contains("خالد دفع") || text.contains("خالد حاسب") -> "خالد"
            text.contains("سارة دفعت") || text.contains("سارة حاسبت") -> "سارة"
            text.contains("محمود دفع") || text.contains("محمود حاسب") -> "محمود"
            text.contains("حاسب عني") || text.contains("دفع عني") -> "شخص آخر"
            else -> "أنا"
        }
    }

    private fun detectBeneficiary(text: String): String {
        return when {
            text.contains("للبيت") || text.contains("لأهلي") || text.contains("للمنزل") -> "البيت"
            text.contains("للجميع") || text.contains("عن الشباب") || text.contains("لنا كلنا") ||
            text.contains("مشترك") -> "الجميع"
            else -> "شخصي"
        }
    }

    private fun extractAmount(text: String): Double {
        val cleanedOfFuelGrades = text
            .replace(Regex("""بنزين\s*91\b"""), "بنزين_واحد_وتسعين")
            .replace(Regex("""بنزين\s*95\b"""), "بنزين_خمسة_وتسعين")

        val englishNormalized = normalizeDigits(cleanedOfFuelGrades)

        // Check for textual amounts first
        val textualAmount = when {
            englishNormalized.contains("مية وخمسين") || englishNormalized.contains("مئة وخمسون") -> 150.0
            englishNormalized.contains("مية") || englishNormalized.contains("مئة") -> 100.0
            englishNormalized.contains("خمسين") || englishNormalized.contains("خمسون") -> 50.0
            englishNormalized.contains("عشرين") || englishNormalized.contains("عشرون") -> 20.0
            englishNormalized.contains("ثلاثين") || englishNormalized.contains("ثلاثون") -> 30.0
            englishNormalized.contains("أربعين") || englishNormalized.contains("أربعون") -> 40.0
            englishNormalized.contains("عشرة") || englishNormalized.contains("عشر") -> 10.0
            englishNormalized.contains("خمسة") -> 5.0
            else -> 0.0
        }

        // Regex for digits (e.g. 50, 85.50, بـ 18)
        val regex = Regex("""(?:بـ|ب|بقيمة|مبلغ|\s|^)(\d+(?:[.,]\d+)?)\b""")
        val match = regex.find(englishNormalized)
        if (match != null) {
            val numStr = match.groupValues[1].replace(",", ".")
            val parsed = numStr.toDoubleOrNull() ?: 0.0
            if (parsed > 0.0) return parsed
        }

        return textualAmount
    }

    private fun normalizeDigits(input: String): String {
        val easternArabic = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        var result = input
        for (i in easternArabic.indices) {
            result = result.replace(easternArabic[i], ('0' + i))
        }
        return result
    }
}
