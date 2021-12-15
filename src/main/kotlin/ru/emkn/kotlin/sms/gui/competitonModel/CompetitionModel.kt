package ru.emkn.kotlin.sms.gui.competitonModel

import ru.emkn.kotlin.sms.ParticipantCheckpointTime
import ru.emkn.kotlin.sms.gui.ModelListener

/**
 * This class models the competition process.
 * It stores a list of [ParticipantCheckpointTime] triples.
 *
 * Every time the model changes, it notifies all listeners
 * via [ModelListener] interface.
 */
class CompetitionModel {
    // actual list of ParticipantCheckpointTime triples
    private val timestamps: MutableList<ParticipantCheckpointTime> = mutableListOf()

    private val listeners: MutableList<ModelListener<CompetitionModel>> = mutableListOf()
    fun addListener(listener: ModelListener<CompetitionModel>) {
        listeners.add(listener)
    }

    private fun notifyAllListeners() {
        listeners.forEach {
            it.modelChanged(this)
        }
    }
}