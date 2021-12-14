package ru.emkn.kotlin.sms.gui

import ru.emkn.kotlin.sms.ParticipantCheckpointTime

class CompetitionModel {
    val timestamps: MutableList<ParticipantCheckpointTime> = mutableListOf()

    private val listeners: MutableList<CompetitionModelListener> = mutableListOf()
    fun addListener(listener: CompetitionModelListener) {
        listeners.add(listener)
    }

    private fun notifyAllListeners() {
        listeners.forEach {
            it.modelChanged(this)
        }
    }
}