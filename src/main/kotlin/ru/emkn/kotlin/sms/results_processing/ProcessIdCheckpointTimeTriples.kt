package ru.emkn.kotlin.sms.results_processing

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.CheckpointAndTime
import ru.emkn.kotlin.sms.IdWithLiveResult
import ru.emkn.kotlin.sms.ParticipantsList


internal fun processIdCheckpointTimeList(
    listOfIdCheckpointAndTime: List<IdAndCheckpointAndTime>,
    participantsList: ParticipantsList,
): List<IdWithLiveResult> {
    val groupedById = listOfIdCheckpointAndTime.groupBy({ it.id }) {
        CheckpointAndTime(it.checkpoint, it.time)
    }
    return groupedById.map { (id, checkpointsToTimes) ->
        Logger.debug("Begin to calculate the time of participant #$id.")
        val result = participantsList.getRouteOfId(id)!!.calculateLiveResult(
            checkpointsToTimes,
            participantsList.getStartingTimeOfId(id)!!,
        )
        Logger.debug("Ended the calculation time of participant #$id.")

        IdWithLiveResult(id, result)
    }
}
