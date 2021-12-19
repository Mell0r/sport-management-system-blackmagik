package ru.emkn.kotlin.sms.gui.builders

import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.StartingProtocol
import ru.emkn.kotlin.sms.time.Time

/**
 * Stores starting times for Participants.
 */
abstract class StartingTimes(
    internal val mapping: Map<Participant, Time> = mapOf(),
) {
    fun getStartingTimeOfOrNull(participant: Participant): Time? {
        return mapping[participant]
    }

    /**
     * Creates a list of [StartingProtocol]s based on data in [StartingTimes].
     * Then, starting protocols can be dumped to CSV, etc.
     */
    fun toStartingProtocols(): List<StartingProtocol> {
        TODO()
    }
}