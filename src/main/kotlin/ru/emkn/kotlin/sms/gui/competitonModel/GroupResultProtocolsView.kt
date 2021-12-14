package ru.emkn.kotlin.sms.gui.competitonModel

import ru.emkn.kotlin.sms.GroupResultProtocol

class GroupResultProtocolsView : CompetitionModelListener {
    var protocols: MutableList<GroupResultProtocol> = mutableListOf()

    override fun modelChanged(model: CompetitionModel) {
        TODO()
    }
}