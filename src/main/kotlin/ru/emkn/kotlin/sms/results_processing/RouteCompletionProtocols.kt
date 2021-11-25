package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.time.Time

typealias CheckpointLabelT = String

data class RouteCompletionByParticipantEntry(
    val checkpointLabel: CheckpointLabelT,
    val time: Time
)

class RouteCompletionByParticipantProtocol(
    val id: Int,
    val checkpointTimes: List<RouteCompletionByParticipantEntry>
)

data class RouteCompletionByCheckpointEntry(
    val id: Int,
    val time: Time
)

data class RouteCompletionByCheckpointProtocol(
    val checkpointLabel: CheckpointLabelT,
    val participantTimes: List<RouteCompletionByCheckpointEntry>
)