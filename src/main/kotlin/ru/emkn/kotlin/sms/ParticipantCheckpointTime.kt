package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.time.Time

data class ParticipantCheckpointTime(
    val participant: Participant,
    val checkpoint: CheckpointLabelT,
    val time: Time,
)