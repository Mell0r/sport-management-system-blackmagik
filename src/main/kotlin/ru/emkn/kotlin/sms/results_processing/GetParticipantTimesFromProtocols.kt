package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.CheckpointLabelT
import ru.emkn.kotlin.sms.ParticipantsList
import ru.emkn.kotlin.sms.IdWithFinalResult
import ru.emkn.kotlin.sms.time.Time

internal data class IdAndCheckpointAndTime(
    val id: Int, val checkpoint: CheckpointLabelT, val time: Time
)

fun getParticipantsTimesFromParticipantTimestampsProtocols(
    routeCompletionProtocols: List<ParticipantTimestampsProtocol>,
    participantsList: ParticipantsList,
): List<IdWithFinalResult> {

    val listOfIdAndCheckpointAndTimes =
        routeCompletionProtocols.flatMap { (id, checkpointTimes) ->
            checkpointTimes.map { (checkpointLabel, time) ->
                IdAndCheckpointAndTime(id, checkpointLabel, time)
            }
        }

    return processIdCheckpointTimeList(
        listOfIdAndCheckpointAndTimes,
        participantsList,
    ).map { it.toIdWithFinalResult() }
}


fun getParticipantsTimesFromCheckpointTimestampsProtocols(
    routeCompletionProtocols: List<CheckpointTimestampsProtocol>,
    participantsList: ParticipantsList,
): List<IdWithFinalResult> {
    val listOfIdCheckpointTimes =
        routeCompletionProtocols.flatMap { (checkpointLabel, participantTimes) ->
            participantTimes.map { (id, time) ->
                IdAndCheckpointAndTime(id, checkpointLabel, time)
            }
        }

    return processIdCheckpointTimeList(
        listOfIdCheckpointTimes,
        participantsList,
    ).map { it.toIdWithFinalResult() }
}