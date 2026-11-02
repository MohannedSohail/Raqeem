package org.moh.raqeem.project.ai.nlp

import org.moh.raqeem.project.model.ExpenseCategory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LocalDialectExpenseParserTest {

    private val parser = LocalDialectExpenseParser()

    @Test
    fun testMultiItemDialectParsingWithShekels() {
        val transcript = "عبيت بنزين 91 بخمسين شيكل، ومحمد حاسب قهوة بـ 18"
        val batch = parser.parseTranscript(transcript)

        assertEquals("شيكل", batch.currency, "Default currency must be Shekel (شيكل)")
        assertEquals(2, batch.items.size)

        // Item 1: Petrol
        val petrolItem = batch.items[0]
        assertEquals("بنزين 91", petrolItem.title)
        assertEquals(50.0, petrolItem.amount)
        assertEquals(ExpenseCategory.TRANSPORTATION, petrolItem.category)
        assertEquals("أنا", petrolItem.paidBy)

        // Item 2: Coffee by Mohammed
        val coffeeItem = batch.items[1]
        assertEquals("قهوة ومشروبات", coffeeItem.title)
        assertEquals(18.0, coffeeItem.amount)
        assertEquals(ExpenseCategory.DINING, coffeeItem.category)
        assertEquals("محمد", coffeeItem.paidBy)

        assertEquals(68.0, batch.totalAmount)
        assertTrue(batch.sourceEngine.contains("Whisper.cpp"))
    }

    @Test
    fun testShekelSlangAndNISVariants() {
        val shekelBatch = parser.parseTranscript("شريت تموينات بـ 75 شيكل ومحمد دفع 25 شواكل")
        assertEquals("شيكل", shekelBatch.currency)
        assertEquals(100.0, shekelBatch.totalAmount)

        val nisBatch = parser.parseTranscript("دفعت 80 NIS عشاء مطعم")
        assertEquals("شيكل", nisBatch.currency)
        assertEquals(80.0, nisBatch.totalAmount)
    }

    @Test
    fun testCurrencyDetectionForRegionalDialects() {
        val egyptian = parser.parseTranscript("دفعت 150 جنيه عشاء في مطعم")
        assertEquals("EGP", egyptian.currency)
        assertEquals(150.0, egyptian.totalAmount)
        assertEquals(ExpenseCategory.DINING, egyptian.items[0].category)

        val emirati = parser.parseTranscript("حاسبت 45 درهم بقالة وتموينات")
        assertEquals("AED", emirati.currency)
        assertEquals(45.0, emirati.totalAmount)
        assertEquals(ExpenseCategory.GROCERIES, emirati.items[0].category)
    }

    @Test
    fun testBeneficiaryAndPayerIdentification() {
        val homeGroceries = parser.parseTranscript("أغراض سوبرماركت 200 شيكل للبيت")
        assertEquals("البيت", homeGroceries.items[0].beneficiary)
        assertEquals("أنا", homeGroceries.items[0].paidBy)
        assertEquals("شيكل", homeGroceries.currency)

        val groupDinner = parser.parseTranscript("عشاء 120 شيكل للجميع وأحمد دفع")
        assertEquals("الجميع", groupDinner.items[0].beneficiary)
        assertEquals("أحمد", groupDinner.items[0].paidBy)
    }

    @Test
    fun testBankSmsOfflineParsingWithShekels() {
        val sms = "شراء بقيمة 85.50 شيكل من سوبرماركت بطاقة مدى"
        val batch = parser.parseTranscript(sms)

        assertEquals(85.50, batch.totalAmount)
        assertEquals("شيكل", batch.currency)
        assertEquals(ExpenseCategory.GROCERIES, batch.items[0].category)
    }
}
