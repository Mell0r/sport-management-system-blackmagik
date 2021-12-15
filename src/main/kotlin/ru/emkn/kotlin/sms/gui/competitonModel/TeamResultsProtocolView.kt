package ru.emkn.kotlin.sms.gui.competitonModel

import ru.emkn.kotlin.sms.ParticipantCheckpointTime
import ru.emkn.kotlin.sms.TeamResultsProtocol
import ru.emkn.kotlin.sms.gui.builders.BuilderListener

class TeamResultsProtocolView : CompetitionModelListener  {
    var protocol: TeamResultsProtocol = TeamResultsProtocol(listOf())

    override fun modelChanged(timestamps: List<ParticipantCheckpointTime>) {
        TODO()
    }
}