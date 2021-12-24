package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.Participant

data class ParticipantWithFinalResult(
    val participant: Participant,
    val result: FinalParticipantResult,
)
