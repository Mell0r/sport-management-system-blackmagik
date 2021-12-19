package ru.emkn.kotlin.sms.gui.builders

import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.time.Time

class FixedStartingTimes(
    mapping: Map<Participant, Time> = mapOf()
) : StartingTimes(mapping) {
    fun getStartingTimeOf(participant: Participant): Time {
        return mapping[participant]
            ?: throw InternalError("In the fixed list of starting times there is no starting time of participant $participant")
    }
}