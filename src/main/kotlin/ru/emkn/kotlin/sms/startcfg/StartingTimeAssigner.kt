package ru.emkn.kotlin.sms.startcfg

import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.ParticipantsList

/**
 * An algorithm which assigns starting times to [ProcessedApplicant]s,
 * turning them into [Participant]s.
 *
 * Ids are assigned by [Participant] constructor.
 */
interface StartingTimeAssigner {
    fun assign(processedApplicants: List<ProcessedApplicant>): ParticipantsList
}