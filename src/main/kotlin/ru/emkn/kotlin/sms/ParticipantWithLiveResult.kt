package ru.emkn.kotlin.sms

data class ParticipantWithLiveResult(
    val participant: Participant,
    val liveResult: LiveParticipantResult,
) {
    fun toIdWithFinalResult() = IdWithFinalResult(
        id = participant.id,
        result = liveResult.toFinalParticipantResult(),
    )
}
