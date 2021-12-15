package ru.emkn.kotlin.sms.gui.builders

import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.ParticipantsList
import ru.emkn.kotlin.sms.gui.ModelListener

class ParticipantsListBuilder {
    private val listBuilder = UniqueListBuilder<Participant>(
        equals = { participant1, participant2 ->
            participant1.id == participant2.id
        }
    )

    private val listeners: MutableList<ModelListener<ParticipantsListBuilder>> = mutableListOf()
    fun addListener(listener: ModelListener<ParticipantsListBuilder>) {
        listeners.add(listener)
    }

    private fun notifyAllListeners() {
        listeners.forEach {
            it.modelChanged(this)
        }
    }

    fun build(): ParticipantsList {
        TODO()
    }

    private fun checkModification(modified: Boolean) : Boolean {
        if (modified) {
            notifyAllListeners()
        }
        return modified
    }

    /**
     * @return true if it successfully added [participant],
     * false if a participant with the same id already present in the list.
     */
    fun addParticipant(participant: Participant) : Boolean {
        return checkModification(listBuilder.add(participant))
    }

    /**
     * @return true if it successfully removed [participant],
     * false if [participant] was not present in the list.
     */
    fun removeParticipant(participant: Participant) : Boolean {
        return checkModification(listBuilder.remove(participant))
    }
}