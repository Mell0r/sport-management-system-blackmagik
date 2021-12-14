package ru.emkn.kotlin.sms.gui.competitonModel

import ru.emkn.kotlin.sms.ParticipantCheckpointTime

/**
 * A controller for [CompetitionModel].
 * Actual controller depends on the program mode.
 */
abstract class CompetitionModelController(val model: CompetitionModel) {
    abstract fun addTimestamp(timestamp: ParticipantCheckpointTime)
    abstract fun removeTimestamp(timestamp: ParticipantCheckpointTime)
}