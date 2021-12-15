package ru.emkn.kotlin.sms.gui.competitonModel

import ru.emkn.kotlin.sms.GroupResultProtocol
import ru.emkn.kotlin.sms.gui.ModelListener

class GroupResultProtocolsView : ModelListener<CompetitionModel> {
    var protocols: MutableList<GroupResultProtocol> = mutableListOf()

    override fun modelChanged(model: CompetitionModel) {
        TODO()
    }
}