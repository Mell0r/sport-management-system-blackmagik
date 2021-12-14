package ru.emkn.kotlin.sms.gui.builders

import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.time.Time

class MutableStartingTimes (
    private val mutableMapping: MutableMap<Participant, Time> = mutableMapOf()
) : StartingTimes(mutableMapping) {
    fun setStartingTimeOf(participant: Participant, time: Time) {
        mutableMapping[participant] = time
    }
}