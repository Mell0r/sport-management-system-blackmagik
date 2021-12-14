package ru.emkn.kotlin.sms.gui

import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.ParticipantsList

class ParticipantsListBuilder {
    val listBuilder = UniqueListBuilder<Participant>(
        equals = { participant1, participant2 ->
            participant1.id == participant2.id
        }
    )

    fun build(): ParticipantsList {
        TODO()
    }
}