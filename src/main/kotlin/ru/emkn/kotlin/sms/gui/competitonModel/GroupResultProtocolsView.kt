package ru.emkn.kotlin.sms.gui.competitonModel

import ru.emkn.kotlin.sms.GroupResultProtocol
import ru.emkn.kotlin.sms.ParticipantCheckpointTime
import ru.emkn.kotlin.sms.gui.builders.BuilderListener

class GroupResultProtocolsView : CompetitionModelListener {
    var protocols: MutableList<GroupResultProtocol> = mutableListOf()

    override fun modelChanged(timestamps: List<ParticipantCheckpointTime>) {
        TODO()
    }
}