package ru.emkn.kotlin.sms.gui.builders

import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.time.Time

/**
 * Stores starting times for Participants.
 */
abstract class StartingTimes(
    internal val mapping: Map<Participant, Time> = mutableMapOf()
) {
    fun getStartingTimeOfOrNull(participant: Participant) : Time? {
        return mapping[participant]
    }
}