package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.Route
import ru.emkn.kotlin.sms.time.Time
import kotlin.test.Test
import kotlin.test.assertEquals

private fun Int.s() = Time(this)


internal class GetParticipantsTimesFromByParticipantProtocolsTest {
    private fun quickProtocolEntryList(
        firstTime: Int,
        secondTime: Int,
        thirdTime: Int
    ) =
        listOf(
            CheckpointLabelAndTime("1", firstTime.s()),
            CheckpointLabelAndTime("2", secondTime.s()),
            CheckpointLabelAndTime("3", thirdTime.s()),
        )

    @Test
    fun testParticipantsTimesFromByParticipantProtocols() {
        val mainRoute = Route("name", listOf("1", "2", "3"))
        val startTimeGetter: (Int) -> Time = { 0.s() }
        val mainRouteGetter: (Int) -> Route = { mainRoute }
        val protocols = listOf(
            ParticipantTimestampsProtocol(
                1,
                quickProtocolEntryList(1, 2, 3)
            ),
            ParticipantTimestampsProtocol(
                2,
                quickProtocolEntryList(2, 1, 6)
            ),
            ParticipantTimestampsProtocol(
                3,
                quickProtocolEntryList(1, 4, 9)
            ),
        )
        val results = getParticipantsTimesFromParticipantTimestampsProtocols(
            protocols,
            mainRouteGetter,
            startTimeGetter
        )
        assertEquals(3, results[1])
        assertEquals(null, results[2])
        assertEquals(9, results[3])

    }
}