package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.time.Time
import ru.emkn.kotlin.sms.*

/**
 * Base entry for all checkpoint data.
 */
data class ParticipantCheckpointTime(
    val participant: Participant,
    val checkpoint: CheckpointLabelT,
    val time: Time,
) {
    fun toCheckPointAndTime() = CheckpointAndTime(checkpoint, time)
}

