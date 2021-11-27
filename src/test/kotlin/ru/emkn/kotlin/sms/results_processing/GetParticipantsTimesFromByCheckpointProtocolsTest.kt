package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.Route
import ru.emkn.kotlin.sms.time.Time
import kotlin.test.Test
import kotlin.test.assertEquals

private fun Int.s() = Time(this)
internal class GetParticipantsTimesFromByCheckpointProtocolsTest {

    @Test
    fun testGetParticipantsTimesFromByCheckpointProtocols() {
        /*
        1: 1 4 6
        2: 1 2 3
        3: 5 2 4
         */
        val mainRoute = Route("name", listOf("1", "2", "3"))
        val startTimeGetter: (Int) -> Time = { 0.s() }
        val mainRouteGetter: (Int) -> Route = { mainRoute }
        fun idToTime(id: Int, time: Time) =
            IdAndTime(id, time)

        val protocols = listOf(
            RouteCompletionByCheckpointProtocol(
                "1",
                listOf(
                    idToTime(1, 1.s()),
                    idToTime(2, 1.s()),
                    idToTime(3, 5.s())
                )
            ), RouteCompletionByCheckpointProtocol(
                "2",
                listOf(
                    idToTime(1, 4.s()),
                    idToTime(2, 2.s()),
                    idToTime(3, 2.s())
                )
            ), RouteCompletionByCheckpointProtocol(
                "3",
                listOf(idToTime(1, 6.s()), idToTime(2, 3.s()), idToTime(3, 4.s()))
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