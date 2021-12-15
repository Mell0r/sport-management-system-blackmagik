package ru.emkn.kotlin.sms.gui.competitonModel

import ru.emkn.kotlin.sms.TeamResultsProtocol
import ru.emkn.kotlin.sms.gui.ModelListener

class TeamResultsProtocolView : ModelListener<CompetitionModel> {
    var protocols: TeamResultsProtocol = TeamResultsProtocol(listOf())

    override fun modelChanged(model: CompetitionModel) {
        TODO()
    }
}