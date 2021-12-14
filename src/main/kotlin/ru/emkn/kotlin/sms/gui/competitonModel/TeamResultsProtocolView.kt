package ru.emkn.kotlin.sms.gui.competitonModel

import ru.emkn.kotlin.sms.TeamResultsProtocol

class TeamResultsProtocolView : CompetitionModelListener {
    var protocols: TeamResultsProtocol = TeamResultsProtocol(listOf())

    override fun modelChanged(model: CompetitionModel) {
        TODO()
    }
}