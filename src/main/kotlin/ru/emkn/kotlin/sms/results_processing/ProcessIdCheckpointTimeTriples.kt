package ru.emkn.kotlin.sms.results_processing

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.ParticipantIdAndTime


internal fun processIdCheckpointTimeList(
    listOfIdCheckpointAndTime: List<IdAndCheckpointAndTime>,
    helper: Helper
): List<ParticipantIdAndTime> {
    val groupedById = listOfIdCheckpointAndTime.groupBy({ it.id }) {
        CheckpointLabelAndTime(it.checkpoint, it.time)
    }
    return groupedById.map { (id, checkpointsToTimes) ->
        Logger.debug("Begin to calculate the time of participant #$id.")
        val time = helper.getRouteOf(id).calculateResultingTime(
            checkpointsToTimes,
            helper.getStartingTimeOf(id)
        )
        Logger.debug("Ended the calculation time of participant #$id.")

        ParticipantIdAndTime(id, time)
    }
}
