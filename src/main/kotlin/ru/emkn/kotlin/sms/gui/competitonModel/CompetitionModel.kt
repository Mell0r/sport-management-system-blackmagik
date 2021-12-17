package ru.emkn.kotlin.sms.gui.competitonModel

import ru.emkn.kotlin.sms.ParticipantCheckpointTime
import ru.emkn.kotlin.sms.results_processing.CheckpointTimestampsProtocol
import ru.emkn.kotlin.sms.results_processing.ParticipantTimestampsProtocol

/**
 * This class models the competition process.
 * It stores a list of [ParticipantCheckpointTime] triples.
 *
 * Every time the model changes, it notifies all listeners
 * via [CompetitionModelListener] interface.
 */
class CompetitionModel {
    // actual list of ParticipantCheckpointTime triples
    val timestamps: MutableList<ParticipantCheckpointTime> = mutableListOf()

    private val listeners: MutableList<CompetitionModelListener> = mutableListOf()
    fun addListener(listener: CompetitionModelListener) {
        listeners.add(listener)
    }

    private fun notifyAllListeners() {
        listeners.forEach {
            it.modelChanged(timestamps)
        }
    }

    inner class Controller {
        fun addTimestamp(timestamp: ParticipantCheckpointTime) {
            TODO()
        }

        fun removeTimestamp(timestamp: ParticipantCheckpointTime) {
            TODO()
        }

        fun addTimestampsFromProtocolsByParticipant(protocols: List<ParticipantTimestampsProtocol>) {
            TODO()
        }

        fun addTimestampsFromProtocolsByCheckpoint(protocols: List<CheckpointTimestampsProtocol>) {
            TODO()
        }

        fun addTimestampsFromProtocolFilesByParticipant(filePaths: List<String>) {
            TODO()
        }

        fun addTimestampsFromProtocolFilesByCheckpoint(filePaths: List<String>) {
            TODO()
        }

        fun clearAllTimestamps() {
            TODO()
        }
    }
}