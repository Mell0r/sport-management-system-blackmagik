package ru.emkn.kotlin

import org.junit.Test
import ru.emkn.kotlin.sms.StartingProtocol
import ru.emkn.kotlin.sms.StartingProtocolEntry
import ru.emkn.kotlin.sms.time.Time
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class StartingProtocolOutputTest {
    private val startingProtocolTest1 = StartingProtocol(listOf(
        StartingProtocolEntry(1, Time(0, 0, 0)),
        StartingProtocolEntry(2, Time(0, 0, 1)),
        StartingProtocolEntry(3, Time(0, 10, 20))
    ), "M10")

    private val startingProtocolTest2 = StartingProtocol(listOf(
        StartingProtocolEntry(15, Time(5, 59, 0)),
        StartingProtocolEntry(10, Time(21, 53, 5)),
        StartingProtocolEntry(80, Time(23,59,59))
    ), "TestGroup")

    @Test
    fun getFileNameTest() {
        assertEquals(startingProtocolTest1.getFileName(), "Starting_protocol_of_'M10'_group")
        assertEquals(startingProtocolTest2.getFileName(), "Starting_protocol_of_'TestGroup'_group")
    }

    @Test
    fun getFileContentTest() {
        assertTrue { startingProtocolTest1.getFileContent() == listOf("1,00:00:00", "2,00:00:01", "3,00:10:20") }
        assertTrue { startingProtocolTest2.getFileContent() == listOf("15,05:59:00", "10,21:53:05", "80,23:59:59") }
    }
}