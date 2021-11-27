package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.Route
import ru.emkn.kotlin.sms.time.Time

internal data class IdAndCheckpointAndTime(
    val id: Int, val checkpoint: CheckpointLabelT, val time: Time
)

/**
 * @param routeCompletionProtocols  completion protocols
 * @param idToRoute  function that maps the participant's id to his route
 * @param idToTime  function that maps the participant's id to his start time
 * @return map M such that m.at(participantId) = participantTimeInSeconds and
 * <code> m.at(participantId) </code> === null if the participant is disqualified. The map M contains
 * only people mentioned in protocols.
 */

fun getParticipantsTimesFromByCheckpointProtocols(
    routeCompletionProtocols: List<RouteCompletionByCheckpointProtocol>,
    idToRoute: (Int) -> Route,
    idToTime: (Int) -> Time
): Map<Int, Int?> {
    val listOfIdCheckpointTimes =
        routeCompletionProtocols.flatMap { protocol ->
            val checkpointLabel = protocol.checkpointLabel
            protocol.participantTimes.map { (id, time) ->
                IdAndCheckpointAndTime(id, checkpointLabel, time)
            }
        }

    return processIdCheckpointTimeList(
        listOfIdCheckpointTimes,
        idToTime,
        idToRoute
    )
}

internal fun processIdCheckpointTimeList(
    listOfIdCheckpointAndTime: List<IdAndCheckpointAndTime>,
    idToTime: (Int) -> Time,
    idToRoute: (Int) -> Route
): Map<Int, Int?> {
    val groupedById = listOfIdCheckpointAndTime.groupBy({ it.id }) {
        CheckpointLabelAndTime(it.checkpoint, it.time)
    }
    return groupedById.map { (id, checkpointsToTimes) ->
        generateIdToResultsPair(
            id, checkpointsToTimes, idToTime, idToRoute
        )
    }.toMap()
}

private fun generateIdToResultsPair(
    id: Int,
    checkpointsToTimes: List<CheckpointLabelAndTime>,
    startTimeGetter: (Int) -> Time,
    idToRoute: (Int) -> Route
): Pair<Int, Int?> {
    val checkpointsToTimesChronological =
        checkpointsToTimes.sortedBy { it.time }
    val chronologicalCheckpoints =
        checkpointsToTimesChronological.map { it.checkpointLabel }
    return if (chronologicalCheckpoints != idToRoute(id).route)
        Pair(id, null)
    else {
        val finishTime = checkpointsToTimesChronological.last().time
        val timeForDistance = finishTime - startTimeGetter(id)
        Pair(id, timeForDistance)
    }
}

