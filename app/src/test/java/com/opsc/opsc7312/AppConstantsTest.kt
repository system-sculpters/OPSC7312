package com.opsc.opsc7312

import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat

class AppConstantsTest {
    // This class was adapted from YouTube
    // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
    // CodingWithPuneet
    // https://www.youtube.com/@codingwithpuneet
    @Test
    fun testConvertLongToString() {
        // This method was adapted from YouTube
        // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
        // CodingWithPuneet
        // https://www.youtube.com/@codingwithpuneet
        // Given a timestamp (10 May 2022)
        val timestamp: Long = 1652150400000 // Corresponds to "10/05/2022"
        val expectedDate = "10/05/2022"

        // When converting it to a string
        val actualDate = AppConstants.convertLongToString(timestamp)

        // Then the result should match the expected date format
        assertEquals(expectedDate, actualDate)
    }

    @Test
    fun testFormatAmount() {
        // This method was adapted from YouTube
        // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
        // CodingWithPuneet
        // https://www.youtube.com/@codingwithpuneet
        // Given a double amount
        val amount = 1234.56789
        val expectedFormattedAmount = "1234.57"

        // When formatting the amount
        val actualFormattedAmount = AppConstants.formatAmount(amount)

        // Then the result should match the expected formatted string
        assertEquals(expectedFormattedAmount, actualFormattedAmount)
    }

    @Test
    fun testIsTokenExpired() {
        // This method was adapted from YouTube
        // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
        // CodingWithPuneet
        // https://www.youtube.com/@codingwithpuneet
        // Given an expiration time 1 minute from now
        val expirationTime = System.currentTimeMillis() + 60000

        // When checking if the token has expired
        val isExpired = AppConstants.isTokenExpired(expirationTime)

        // Then the result should be false since it's not expired yet
        assertFalse(isExpired)
    }

    @Test
    fun testTokenExpirationTime() {
        // This method was adapted from YouTube
        // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
        // CodingWithPuneet
        // https://www.youtube.com/@codingwithpuneet
        // When calculating token expiration time (2 days from now)
        val actualExpirationTime = AppConstants.tokenExpirationTime()

        // Then it should be roughly 2 days (in milliseconds)
        val expectedExpirationTime = System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000

        // Assert that the difference is within a reasonable range (e.g., 1 second)
        assertTrue(Math.abs(expectedExpirationTime - actualExpirationTime) < 1000)
    }

    @Test
    fun testConvertStringToLong() {
        // This method was adapted from YouTube
        // https://youtu.be/ssF_YPvLRR8?si=OCxgobZF6XAIF5Z-
        // CodingWithPuneet
        // https://www.youtube.com/@codingwithpuneet
        // Given a date string
        val dateString = "10/05/2022"
        val expectedTimestamp = SimpleDateFormat("dd/MM/yyyy").parse(dateString)?.time ?: 0L

        // When converting it to a long
        val actualTimestamp = AppConstants.convertStringToLong(dateString)

        // Then the result should match the expected timestamp
        assertEquals(expectedTimestamp, actualTimestamp)
    }
}