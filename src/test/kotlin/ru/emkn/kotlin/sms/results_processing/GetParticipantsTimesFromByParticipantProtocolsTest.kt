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
            RouteCompletionByParticipantEntry("1", firstTime.s()),
            RouteCompletionByParticipantEntry("2", secondTime.s()),
            RouteCompletionByParticipantEntry("3", thirdTime.s()),
        )

    @Test
    fun testParticipantsTimesFromByParticipantProtocols() {
        val mainRoute = Route("name", listOf("1", "2", "3"))
        val startTimeGetter: (Int) -> Time = { 0.s() }
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