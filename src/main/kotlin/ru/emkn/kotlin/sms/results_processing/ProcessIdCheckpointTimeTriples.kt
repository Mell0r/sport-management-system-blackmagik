package ru.emkn.kotlin.sms.results_processing

import org.tinylog.kotlin.Logger
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
    if (chronologicalCheckpoints != helper.getRouteOf(id).route) {
        Logger.warn {"Participant $id passed checkpoints in wrong order (expected: ${helper.getRouteOf(id).route}, actual: $chronologicalCheckpoints). Disqualifying."}
        return ParticipantAndTime(id, null)
    } else {
        if (checkpointsToTimes.minOf { it.time } < helper.getStartingTimeOf(id)) {
            Logger.warn {"Participant $id passed his first checkpoint (at ${checkpointsToTimes.minOf { it.time }}) before he is supposed to start (${helper.getStartingTimeOf(id)}). Disqualifying."}
            return ParticipantAndTime(id, null)
        }
        val finishTime = checkpointsToTimesChronological.last().time
        val timeForDistance = finishTime - helper.getStartingTimeOf(id)
        return ParticipantAndTime(id, Time(timeForDistance))
    }
}

