package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.CheckpointLabelT
import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.time.Time

/* Some minor data classes */

data class CheckpointAndTime(
    val checkpointLabel: CheckpointLabelT,
    val time: Time
)

data class ParticipantWithLiveResult(
    val participant: Participant,
    val liveResult: LiveParticipantResult,
) {
    fun toParticipantWithFinalResult() = ParticipantWithFinalResult(
        participant = participant,
        result = liveResult.toFinalParticipantResult(),
    )
}

data class ParticipantWithFinalResult(
    val participant: Participant,
    val result: FinalParticipantResult,
)

data class TeamToScore(val team: String, val score: Int)

data class IdAndTime(
    val id: Int,
    val time: Time
)
