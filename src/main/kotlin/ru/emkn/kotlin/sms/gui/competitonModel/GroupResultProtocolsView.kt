package ru.emkn.kotlin.sms.gui.competitonModel

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.GroupResultProtocol
import ru.emkn.kotlin.sms.ParticipantIdAndTime
import ru.emkn.kotlin.sms.ParticipantCheckpointTime
import ru.emkn.kotlin.sms.gui.builders.FixedStartingTimes
import ru.emkn.kotlin.sms.results_processing.CheckpointLabelAndTime

class GroupResultProtocolsView(
) : CompetitionModelListener {
    var protocols: MutableList<GroupResultProtocol> = mutableListOf()

    override fun modelChanged(timestamps: List<ParticipantCheckpointTime>) {
        TODO()
    }
}