package ru.emkn.kotlin.sms.gui.competitionModel

import com.github.michaelbull.result.*
import ru.emkn.kotlin.sms.results_processing.ParticipantCheckpointTime
import ru.emkn.kotlin.sms.UnitOrMessage
import ru.emkn.kotlin.sms.gui.programState.ProgramState
import ru.emkn.kotlin.sms.results_processing.CheckpointTimestampsProtocol
import ru.emkn.kotlin.sms.results_processing.ParticipantTimestampsProtocol
import ru.emkn.kotlin.sms.results_processing.TimestampsProtocolProcessor
import java.io.File

/**
 * This class models the competition process.
 * It stores a list of [ParticipantCheckpointTime] triples.
 *
 * Every time the model changes, it notifies all listeners
 * via [CompetitionModelListener] interface.
 */
class CompetitionModel(
    state: ProgramState,
) {
    // actual list of ParticipantCheckpointTime triples
    // private because any modification MUST notify all listeners
    private val timestamps: MutableList<ParticipantCheckpointTime> =
        mutableListOf()

    private val timestampsProtocolProcessor = TimestampsProtocolProcessor(state.participantsList)

    private val listeners: MutableList<CompetitionModelListener> =
        mutableListOf()

    fun addListener(listener: CompetitionModelListener) {
        listeners.add(listener)
        listener.modelChanged(timestamps)
    }

    private fun notifyAllListeners() {
        listeners.forEach {
            it.modelChanged(timestamps)
        }
    }

    inner class Controller {
        fun addTimestamp(timestamp: ParticipantCheckpointTime) {
            timestamps.add(timestamp)
            notifyAllListeners()
        }

        fun addTimestampsFromProtocolsByParticipant(protocols: List<ParticipantTimestampsProtocol>) {
            val timestampsToAdd = timestampsProtocolProcessor.processByParticipant(protocols)
            timestamps.addAll(timestampsToAdd)
            notifyAllListeners()
        }

        fun addTimestampsFromProtocolsByCheckpoint(protocols: List<CheckpointTimestampsProtocol>) {
            val timestampsToAdd = timestampsProtocolProcessor.processByCheckpoint(protocols)
            timestamps.addAll(timestampsToAdd)
            notifyAllListeners()
        }

        fun addTimestampsFromProtocolFilesByParticipant(files: List<File>): UnitOrMessage {
            return ParticipantTimestampsProtocol.readAndParseAll(files).map {
                addTimestampsFromProtocolsByParticipant(it)
            }
        }

        fun addTimestampsFromProtocolFilesByCheckpoint(files: List<File>): UnitOrMessage {
            return CheckpointTimestampsProtocol.readAndParseAll(files).map {
                addTimestampsFromProtocolsByCheckpoint(it)
            }
        }
    }
}