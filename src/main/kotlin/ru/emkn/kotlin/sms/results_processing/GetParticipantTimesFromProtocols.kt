package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.CheckpointLabelT
import ru.emkn.kotlin.sms.ParticipantIdAndTime
import ru.emkn.kotlin.sms.time.Time

internal data class IdAndCheckpointAndTime(
    val id: Int, val checkpoint: CheckpointLabelT, val time: Time
)

fun getParticipantsTimesFromParticipantTimestampsProtocols(
    routeCompletionProtocols: List<ParticipantTimestampsProtocol>,
    helper: Helper
): List<ParticipantIdAndTime> {

    val listOfIdAndCheckpointAndTimes =
        routeCompletionProtocols.flatMap { protocol ->
            val id = protocol.id
            protocol.checkpointTimes.map { (checkpointLabel, time) ->
                IdAndCheckpointAndTime(id, checkpointLabel, time)
            }
        }

    return processIdCheckpointTimeList(
        listOfIdAndCheckpointAndTimes,
        helper
    )
}


fun getParticipantsTimesFromCheckpointTimestampsProtocols(
    routeCompletionProtocols: List<CheckpointTimestampsProtocol>,
    helper: Helper
): List<ParticipantIdAndTime> {
    val listOfIdCheckpointTimes =
        routeCompletionProtocols.flatMap { protocol ->
            val checkpointLabel = protocol.checkpointLabel
            protocol.participantTimes.map { (id, time) ->
                IdAndCheckpointAndTime(id, checkpointLabel, time)
            }
        }

    return processIdCheckpointTimeList(
        listOfIdCheckpointTimes,
        helper
    )
}