package ru.emkn.kotlin.sms.gui.competitionModel

import com.github.michaelbull.result.mapEither
import ru.emkn.kotlin.sms.results_processing.ParticipantCheckpointTime
import ru.emkn.kotlin.sms.UnitOrMessage
import ru.emkn.kotlin.sms.gui.programState.ProgramState
import ru.emkn.kotlin.sms.io.ReadFailException
import ru.emkn.kotlin.sms.io.WrongFormatException
import ru.emkn.kotlin.sms.io.readAndParseAllFiles
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
    private val state: ProgramState,
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

        fun addTimestampsFromProtocolFilesByParticipant(filePaths: List<String>): UnitOrMessage {
            val protocolsByParticipantOrError =
                com.github.michaelbull.result.runCatching {
                    readAndParseAllFiles(
                        files = filePaths.map { File(it) },
                        competition = state.competition,
                        parser = ParticipantTimestampsProtocol::readFromCsvContentAndCompetition,
                    )
                }
            return protocolsByParticipantOrError
                .mapEither(
                    success = { protocols ->
                        addTimestampsFromProtocolsByParticipant(protocols)
                    },
                    failure = { exception ->
                        when (exception) {
                            is ReadFailException -> exception.message
                            is WrongFormatException -> exception.message
                            else -> throw exception // propagate the exception if we cannot handle it here
                        }
                    }
                )
        }

        fun addTimestampsFromProtocolFilesByCheckpoint(filePaths: List<String>): UnitOrMessage {
            val protocolsByCheckpointOrError =
                com.github.michaelbull.result.runCatching {
                    readAndParseAllFiles(
                        files = filePaths.map { File(it) },
                        competition = state.competition,
                        parser = CheckpointTimestampsProtocol::readFromCsvContentAndCompetition,
                    )
                }
            return protocolsByCheckpointOrError
                .mapEither(
                    success = { protocols ->
                        addTimestampsFromProtocolsByCheckpoint(protocols)
                    },
                    failure = { exception ->
                        when (exception) {
                            is ReadFailException -> exception.message
                            is WrongFormatException -> exception.message
                            else -> throw exception // propagate the exception if we cannot handle it here
                        }
                    }
                )
        }

    }
}