package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.Route
import ru.emkn.kotlin.sms.time.Time


fun getParticipantsTimesFromByCheckpointProtocols(
    routeCompletionProtocols: List<RouteCompletionByCheckpointProtocol>,
    routeGetterFunction: (Int) -> Route,
    startTimeGetter: (Int) -> Time
): Map<Int, Int?> {
    val listOfIdCheckpointAndTime =
        routeCompletionProtocols.flatMap { protocol ->
            val checkpointLabel = protocol.checkpointLabel
            protocol.participantTimes.map { (id, time) ->
                Triple(id, checkpointLabel, time)
            }
        }

    val groupedById = listOfIdCheckpointAndTime.groupBy({ it.first }) {
        RouteCompletionByParticipantEntry(
            it.second,
            it.third
        )
    }
    return groupedById.map { (id, checkpointsToTimes) ->
        val sortedCheckpointsToTimes = checkpointsToTimes.sortedBy { it.time }
        if (sortedCheckpointsToTimes.map{it.checkpointLabel} != routeGetterFunction(id).route)
            Pair(id, null)
        else {
            Pair(
                id,
                sortedCheckpointsToTimes.last().time - startTimeGetter(id)
            )
        }
    }.toMap()
}

