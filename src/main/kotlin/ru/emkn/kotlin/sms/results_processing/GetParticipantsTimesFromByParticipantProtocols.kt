package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.Route
import ru.emkn.kotlin.sms.time.Time

/**
 * @param routeCompletionProtocols  completion protocols
 * @param idToRoute  function that maps the participant's id to his route
 * @param idToTime  function that maps the participant's id to his start time
 * @return map M such that m.at(participantId) = participantTimeInSeconds and
 * <code> m.at(participantId) </code> === null if the participant is disqualified. The map M contains
 * only people mentioned in protocols.
 */
fun getParticipantsTimesFromByParticipantProtocols(
    routeCompletionProtocols: List<RouteCompletionByParticipantProtocol>,
    idToRoute: (Int) -> Route,
    idToTime: (Int) -> Time
): Map<Int, Int?> {

    val listOfIdAndCheckpointAndTimes =
        routeCompletionProtocols.flatMap { protocol ->
            val id = protocol.id
            protocol.checkpointTimes.map { (checkpointLabel, time) ->
                IdAndCheckpointAndTime(id, checkpointLabel, time)
            }
        }

    return processIdCheckpointTimeList(
        listOfIdAndCheckpointAndTimes,
        idToTime,
        idToRoute
    )
}

