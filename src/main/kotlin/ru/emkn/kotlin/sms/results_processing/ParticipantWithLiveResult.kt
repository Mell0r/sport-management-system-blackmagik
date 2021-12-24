package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.Participant

data class ParticipantWithLiveResult(
    val participant: Participant,
    val liveResult: LiveParticipantResult,
) {
    fun toIdWithFinalResult() = IdWithFinalResult(
        id = participant.id,
        result = liveResult.toFinalParticipantResult(),
    )
}
