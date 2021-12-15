package ru.emkn.kotlin.sms.gui.builders

import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.gui.ModelListener
import ru.emkn.kotlin.sms.time.Time

class MutableStartingTimes (
    private val mutableMapping: MutableMap<Participant, Time> = mutableMapOf()
) : StartingTimes(mutableMapping) {

    private val listeners: MutableList<ModelListener<MutableStartingTimes>> = mutableListOf()
    fun addListener(listener: ModelListener<MutableStartingTimes>) {
        listeners.add(listener)
    }

    private fun notifyAllListeners() {
        listeners.forEach {
            it.modelChanged(this)
        }
    }

    fun setStartingTimeOf(participant: Participant, time: Time) {
        mutableMapping[participant] = time
        notifyAllListeners()
    }
}