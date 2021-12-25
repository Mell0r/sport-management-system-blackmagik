package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.TestDataSet2
import kotlin.test.*

internal class LiveGroupResultProtocolGeneratorTests {
    private val testDataSet = TestDataSet2
    private val testGenerator = LiveGroupResultProtocolGenerator(testDataSet.participantsList)

    @Test
    fun `LiveGroupResultProtocolGenerator generate() correctness test`() {
        testDataSet.liveGroupResultProtocolsTestSets.forEach { (timestamps, expectedProtocols) ->
            assertEquals(
                expectedProtocols,
                testGenerator.generate(timestamps).toSet(),
            )
        }
    }
}