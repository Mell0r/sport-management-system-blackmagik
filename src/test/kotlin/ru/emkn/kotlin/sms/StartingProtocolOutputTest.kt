package ru.emkn.kotlin.sms

import org.junit.Test
import ru.emkn.kotlin.sms.time.Time
import kotlin.test.assertTrue

internal class StartingProtocolOutputTest {
    private val startingProtocolTest1 = StartingProtocol("M10", listOf(
        StartingProtocolEntry(1, Time(0, 0, 0)),
        StartingProtocolEntry(2, Time(0, 0, 1)),
        StartingProtocolEntry(3, Time(0, 10, 20))
    ))

    private val startingProtocolTest2 = StartingProtocol("TestGroup", listOf(
        StartingProtocolEntry(15, Time(5, 59, 0)),
        StartingProtocolEntry(10, Time(21, 53, 5)),
        StartingProtocolEntry(80, Time(23,59,59))
    ))

    @Test
    fun getFileContentTest() {
        assertTrue { startingProtocolTest1.dumpToCsv() == listOf("M10,", "1,00:00:00", "2,00:00:01", "3,00:10:20") }
        assertTrue { startingProtocolTest2.dumpToCsv() == listOf("TestGroup,", "15,05:59:00", "10,21:53:05", "80,23:59:59") }
    }
}