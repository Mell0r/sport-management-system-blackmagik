package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.time.Time

typealias CheckpointLabelT = String

data class CheckpointLabelAndTime(
    val checkpointLabel: CheckpointLabelT,
    val time: Time
)

data class ParticipantTimestampsProtocol(
    val id: Int,
    val checkpointTimes: List<CheckpointLabelAndTime>
)

data class IdAndTime(
    val id: Int,
    val time: Time
)

data class CheckpointTimestampsProtocol(
    val checkpointLabel: CheckpointLabelT,
    val participantTimes: List<IdAndTime>
)