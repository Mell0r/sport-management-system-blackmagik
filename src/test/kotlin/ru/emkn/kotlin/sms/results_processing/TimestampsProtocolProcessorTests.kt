package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.TestDataSet2
import kotlin.test.*

internal class TimestampsProtocolProcessorTests {
    private val testDataSet = TestDataSet2
    private val testProcessor = TimestampsProtocolProcessor(testDataSet.participantsList)

    @Test
    fun `TimestampsProtocolProcessor by participant correctness`() {
        testDataSet.byParticipantsTestSets.forEach { (protocols, expectedTimestamps) ->
            assertEquals(
                expectedTimestamps,
                testProcessor.processByParticipant(protocols).toSet(),
            )
        }
    }

    @Test
    fun `TimestampsProtocolProcessor by checkpoint correctness`() {
        testDataSet.byCheckpointTestSets.forEach { (protocols, expectedTimestamps) ->
            assertEquals(
                expectedTimestamps,
                testProcessor.processByCheckpoint(protocols).toSet(),
            )
        }
    }
}