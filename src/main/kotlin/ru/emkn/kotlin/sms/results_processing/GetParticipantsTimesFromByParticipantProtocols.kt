package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.Route
import ru.emkn.kotlin.sms.time.Time

// null if disqualified
data class ResultEntry(val id: Int, val timeForDistance: Int?)

fun getParticipantsTimesFromByParticipantProtocols(
    routeCompletionProtocols: List<RouteCompletionByParticipantProtocol>,
    routeGetterFunction: (Int) -> Route,
    startTimeGetter: (Int) -> Time
): Map<Int, Int?> {
    val results = routeCompletionProtocols.map { protocol ->
        val route = routeGetterFunction(protocol.id)
        val checkpointTimes = protocol.checkpointTimes.sortedBy { entry ->
            entry.time
        }
        if (checkpointTimes.map { it.checkpointLabel } != route.route)
            Pair(protocol.id, null)
        else {
            Pair(
                protocol.id,
                checkpointTimes.last().time - startTimeGetter(protocol.id)
            )
        }
    }
    return results.toMap()
}

