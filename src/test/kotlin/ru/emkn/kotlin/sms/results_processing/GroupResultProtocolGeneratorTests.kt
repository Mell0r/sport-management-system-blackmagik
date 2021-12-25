package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.TestDataSet2
import kotlin.test.*

internal class GroupResultProtocolGeneratorTests {
    private val testDataSet = TestDataSet2
    private val testGenerator = GroupResultProtocolGenerator(testDataSet.participantsList)

    @Test
    fun `GroupResultProtocolGenerator generate() correctness test`() {
        testDataSet.groupResultProtocolsTestSets.forEach { (timestamps, expectedProtocols) ->
            assertEquals(
                expectedProtocols,
                testGenerator.generate(timestamps).toSet(),
            )
        }
    }
}