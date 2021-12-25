package ru.emkn.kotlin.sms.startcfg

import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.ParticipantsList
import ru.emkn.kotlin.sms.time.*

/**
 * Assigns time to participants in a simple way:
 * arithmetic progression starting from [start] with [step].
 */
class LinearStartingTimeAssigner(
    private val start: Time = Time(12, 0, 0),
    private val step: Time = Time(0, 1, 0),
) : StartingTimeAssigner {
    override fun assign(processedApplicants: List<ProcessedApplicant>): ParticipantsList {
        val list = processedApplicants.mapIndexed { index, processedApplicant ->
            Participant(
                processedApplicant = processedApplicant,
                startingTime = start + step * index,
            )
        }
        return ParticipantsList(list)
    }
}