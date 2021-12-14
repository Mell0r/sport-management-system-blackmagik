package ru.emkn.kotlin.sms.gui.competitonModel

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.ParticipantCheckpointTime

class FinishedCompetitionModelController(model: CompetitionModel) : CompetitionModelController(model) {
    private fun logWarn() {
        Logger.warn {"Somebody tried to modify competition model while it cannot be modified."}
    }
    override fun addTimestamp(timestamp: ParticipantCheckpointTime) {
        logWarn()
    }
    override fun removeTimestamp(timestamp: ParticipantCheckpointTime) {
        logWarn()
    }
}