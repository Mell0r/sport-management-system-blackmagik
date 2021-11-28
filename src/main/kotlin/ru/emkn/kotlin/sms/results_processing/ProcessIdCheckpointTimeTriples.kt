package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.ParticipantAndTime
import ru.emkn.kotlin.sms.time.Time


internal fun processIdCheckpointTimeList(
    listOfIdCheckpointAndTime: List<IdAndCheckpointAndTime>,
    helper: Helper
): List<ParticipantAndTime> {
    val groupedById = listOfIdCheckpointAndTime.groupBy({ it.id }) {
        CheckpointLabelAndTime(it.checkpoint, it.time)
    }
    return groupedById.map { (id, checkpointsToTimes) ->
        generateIdToResultsPair(
            id, checkpointsToTimes, helper
        )
    }
}

private fun generateIdToResultsPair(
    id: Int,
    checkpointsToTimes: List<CheckpointLabelAndTime>,
    helper: Helper
): ParticipantAndTime {
    val checkpointsToTimesChronological =
        checkpointsToTimes.sortedBy { it.time }
    val chronologicalCheckpoints =
        checkpointsToTimesChronological.map { it.checkpointLabel }
    return if (chronologicalCheckpoints != helper.getRouteOf(id).route)
        ParticipantAndTime(helper.getParticipantBy(id), null)
    else {
        val finishTime = checkpointsToTimesChronological.last().time
        val timeForDistance = finishTime - helper.getStartingTimeOf(id)
        ParticipantAndTime(helper.getParticipantBy(id), Time(timeForDistance))
    }
}

