package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.Route
import kotlin.test.Test
import kotlin.test.assertEquals

internal class GetParticipantsTimesFromByParticipantProtocolsTest {
    private fun quickProtocolEntryList(
        firstTime: Int,
        secondTime: Int,
        thirdTime: Int
    ) =
        listOf(
            RouteCompletionByParticipantEntry("1", firstTime),
            RouteCompletionByParticipantEntry("2", secondTime),
            RouteCompletionByParticipantEntry("3", thirdTime),
        )

    @Test
    fun testParticipantsTimesFromByParticipantProtocols() {
        val mainRoute = Route("name", listOf("1", "2", "3"))
        val startTimeGetter: (Int) -> Int = { 0 }
        val mainRouteGetter: (Int) -> Route = { mainRoute }
        val protocols = listOf(
            RouteCompletionByParticipantProtocol(
                1,
                quickProtocolEntryList(1, 2, 3)
            ),
            RouteCompletionByParticipantProtocol(
                2,
                quickProtocolEntryList(2, 1, 6)
            ),
            RouteCompletionByParticipantProtocol(
                3,
                quickProtocolEntryList(1, 4, 9)
            ),
        )
        val results = getParticipantsTimesFromByParticipantProtocols(
            protocols,
            mainRouteGetter,
            startTimeGetter
        )
        assertEquals(3, results[1])
        assertEquals(null, results[2])
        assertEquals(9, results[3])

    }
}