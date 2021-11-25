package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.time.Time
import kotlin.test.*

internal class RouteCompletionProtocolsReadersTest {

    @Test
    fun testReadRouteCompletionByCheckpointProtocol() {
        val protocol = "1km\n1,12:00:00\n2,12:00:02".split("\n")
        assertEquals(RouteCompletionByCheckpointProtocol("1km", listOf(
            RouteCompletionByCheckpointEntry(1, Time(12, 0, 0)),
            RouteCompletionByCheckpointEntry(2, Time(12, 0, 2)),
        )), readRouteCompletionByCheckpointProtocol(protocol)
        )
    }

    @Test
    fun readRouteCompletionByParticipantProtocol() {
        val protocol = "243\n1km,12:00:00\n2km,12:00:02".split("\n")
        assertEquals(RouteCompletionByParticipantProtocol(243, listOf(
            RouteCompletionByParticipantEntry("1km", Time(12, 0, 0)),
            RouteCompletionByParticipantEntry("2km", Time(12, 0, 2)),
        )), readRouteCompletionByParticipantProtocol(protocol)
        )
    }
}