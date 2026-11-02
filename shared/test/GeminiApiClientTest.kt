package org.moh.raqeem.project

import org.moh.raqeem.project.ai.GeminiApiClient
import org.moh.raqeem.project.model.ExpenseCategory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GeminiApiClientTest {

    private val client = GeminiApiClient()

    @Test
    fun heuristicParserExtractsDiningAndPayer() {
        val input = "حاسبت قهوة الصباح بـ 45 ريال مع محمد"
        val batch = client.fallbackHeuristicParser(input)

        assertEquals("SAR", batch.currency)
        assertTrue(batch.items.isNotEmpty())
        val diningItem = batch.items.find { it.category == ExpenseCategory.DINING }
        assertTrue(diningItem != null, "Dining item should be extracted")
        assertEquals(45.0, diningItem.amount)
        assertEquals("محمد", diningItem.paidBy)
    }

    @Test
    fun heuristicParserExtractsTransportation() {
        val input = "عبيت بنزين 70 ريال"
        val batch = client.fallbackHeuristicParser(input)

        val transportItem = batch.items.find { it.category == ExpenseCategory.TRANSPORTATION }
        assertTrue(transportItem != null, "Transportation item should be extracted")
        assertEquals("SAR", batch.currency)
    }

    @Test
    fun heuristicParserExtractsBankSmsNotification() {
        val sms = "تم خصم مبلغ 85.00 ريال لدى متجر كودو للوجبات السريعة"
        val batch = client.fallbackHeuristicParser(sms)

        assertEquals("SAR", batch.currency)
        assertTrue(batch.totalAmount > 0.0)
        assertTrue(batch.items.any { it.category == ExpenseCategory.DINING })
    }
}
