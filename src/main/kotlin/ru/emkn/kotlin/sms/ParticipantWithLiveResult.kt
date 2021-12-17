package ru.emkn.kotlin.sms

data class ParticipantWithLiveResult(
    val participant: Participant,
    val liveResult: LiveParticipantResult,
) {
    val completedCheckpointsOrNegativeINF: Int
        get() = when (liveResult) {
            is LiveParticipantResult.Finished -> participant.group.route.checkpoints.size
            is LiveParticipantResult.InProcess -> liveResult.completedCheckpoints
            is LiveParticipantResult.Disqualified -> Int.MIN_VALUE
        }
}
