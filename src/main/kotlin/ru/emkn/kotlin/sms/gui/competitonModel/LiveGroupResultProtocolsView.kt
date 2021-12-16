package ru.emkn.kotlin.sms.gui.competitonModel

import ru.emkn.kotlin.sms.LiveGroupResultProtocol
import ru.emkn.kotlin.sms.ParticipantCheckpointTime

class LiveGroupResultProtocolsView(
) : CompetitionModelListener {
    var protocols: MutableList<LiveGroupResultProtocol> = mutableListOf()

    override fun modelChanged(timestamps: List<ParticipantCheckpointTime>) {
        TODO()
    }
}