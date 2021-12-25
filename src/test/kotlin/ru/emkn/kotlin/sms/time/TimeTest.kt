package ru.emkn.kotlin.sms.time

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

internal class TimeTest {

    @Test
    fun `throws on bad constructor arguments`() {
        assertFailsWith<IllegalArgumentException> { Time(24, 15, 3) }
        assertFailsWith<IllegalArgumentException> { Time(23, -4, 3) }
        assertFailsWith<IllegalArgumentException> { Time(23, 5, 1200) }
    }

    @Test
    fun `properly compares time points`() {
        assertTrue(Time(15, 3, 23) > Time(15, 3, 22))
        assertTrue(Time(10, 3, 23) < Time(15, 3, 22))
        assertTrue(Time(15, 2, 22) < Time(15, 3, 22))
        assertEquals(Time(15, 3, 22), Time(15, 3, 22))
    }

    @Test
    fun `bad time formats parses should fail`() {
        assertFailsWith<IllegalArgumentException> { Time.fromString("120:2") }
        assertFailsWith<IllegalArgumentException> { Time.fromString("12:2:7:56") }
        assertFailsWith<IllegalArgumentException> { Time.fromString("122:2:7") }
        assertFailsWith<IllegalArgumentException> { Time.fromString("12:2;7") }
    }

    @Test
    fun `properly parses good formats`() {
        assertEquals(Time(15, 3, 16), Time.fromString("15:03:16"))
        assertEquals(Time(12, 0, 0), Time.fromString("12:00:00"))
    }

    @Test
    fun `difference works as expected`() {
        val firstRange = 20_000..20_000 + 20
        val secondRange = 40_000..40_000 + 20
        for (firstSeconds in firstRange) {
            for (secondSeconds in secondRange) {
                assertEquals(
                    secondSeconds - firstSeconds,
                    (Time(secondSeconds) - Time(firstSeconds)).asSeconds()
                )
            }
        }
    }

    @Test
    fun `good seconds constructor`() {
        assertEquals(Time(15, 0, 0), Time(15 * 3600))
        assertEquals(Time(15, 34, 12), Time(15 * 3600 + 34 * 60 + 12))
        assertEquals(Time(0, 0, 0), Time(0))
    }

    @Test
    fun `test string representation`() {
        assertEquals("15:00:00", "${Time(15, 0, 0)}")
        assertEquals("15:00:02", "${Time(15, 0, 2)}")
        assertEquals("15:12:02", "${Time(15, 12, 2)}")
    }
}