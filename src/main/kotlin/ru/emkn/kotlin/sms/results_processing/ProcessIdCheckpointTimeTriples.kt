package ru.emkn.kotlin.sms.results_processing

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.IdWithFinalResult


internal fun processIdCheckpointTimeList(
    listOfIdCheckpointAndTime: List<IdAndCheckpointAndTime>,
    helper: Helper
): List<IdWithFinalResult> {
    val groupedById = listOfIdCheckpointAndTime.groupBy({ it.id }) {
        CheckpointLabelAndTime(it.checkpoint, it.time)
    }
    return groupedById.map { (id, checkpointsToTimes) ->
        Logger.debug("Begin to calculate the time of participant #$id.")
        val time = helper.getRouteOf(id).calculateFinalResult(
            checkpointsToTimes,
            helper.getStartingTimeOf(id)
        )
        Logger.debug("Ended the calculation time of participant #$id.")

        IdWithFinalResult(id, time)
    }
}
