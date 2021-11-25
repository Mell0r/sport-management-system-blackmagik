package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.Route
import ru.emkn.kotlin.sms.time.Time

/**
 * @param routeCompletionProtocols  completion protocols
 * @param routeGetterFunction  function that maps the participant's id to his route
 * @param startTimeGetter  function that maps the participant's id to his start time
 * @return map M such that m.at(participantId) = participantTimeInSeconds and
 * <code> m.at(participantId) </code> === null if the participant is disqualified. The map M contains
 * only people mentioned in protocols.
 */
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

