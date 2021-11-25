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

