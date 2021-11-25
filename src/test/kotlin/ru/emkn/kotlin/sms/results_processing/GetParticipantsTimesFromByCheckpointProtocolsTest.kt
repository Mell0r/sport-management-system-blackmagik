package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.Route
import kotlin.test.Test
import kotlin.test.assertEquals

internal class GetParticipantsTimesFromByCheckpointProtocolsTest {

    @Test
    fun testGetParticipantsTimesFromByCheckpointProtocols() {
        /*
        1: 1 4 6
        2: 1 2 3
        3: 5 2 4
         */
        val mainRoute = Route("name", listOf("1", "2", "3"))
        val startTimeGetter: (Int) -> Int = { 0 }
        val mainRouteGetter: (Int) -> Route = { mainRoute }
        fun idToTime(id: Int, time: Time) =
            RouteCompletionByCheckpointEntry(id, time)

        val protocols = listOf(
            RouteCompletionByCheckpointProtocol(
                "1",
                listOf(idToTime(1, 1), idToTime(2, 1), idToTime(3, 5))
            ), RouteCompletionByCheckpointProtocol(
                "2",
                listOf(idToTime(1, 4), idToTime(2, 2), idToTime(3, 2))
            ), RouteCompletionByCheckpointProtocol(
                "3",
                listOf(idToTime(1, 6), idToTime(2, 3), idToTime(3, 4))
            )
        )
        val results = getParticipantsTimesFromByCheckpointProtocols(
            protocols,
            mainRouteGetter,
            startTimeGetter
        )
        assertEquals(results[1], 6)
        assertEquals(results[2], 3)
        assertEquals(results[3], null)
    }
}