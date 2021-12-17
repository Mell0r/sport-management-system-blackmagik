package ru.emkn.kotlin.sms.gui.competitonModel

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.ParticipantCheckpointTime
import ru.emkn.kotlin.sms.gui.programState.ProgramState
import ru.emkn.kotlin.sms.io.ReadFailException
import ru.emkn.kotlin.sms.io.WrongFormatException
import ru.emkn.kotlin.sms.io.readAndParseAllFiles
import ru.emkn.kotlin.sms.results_processing.CheckpointTimestampsProtocol
import ru.emkn.kotlin.sms.results_processing.ParticipantTimestampsProtocol
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
    private val timestamps: MutableList<ParticipantCheckpointTime> = mutableListOf()

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
            timestamps.add(timestamp)
            notifyAllListeners()
        }

        fun removeTimestamp(timestamp: ParticipantCheckpointTime) {
            require(timestamps.remove(timestamp))
            notifyAllListeners()
        }

        fun addTimestampsFromProtocolsByParticipant(protocols: List<ParticipantTimestampsProtocol>) {
            timestamps.addAll(
                protocols.flatMap { (participantID, checkpointAndTimePairs) ->
                    val participant = state.participantsList.getParticipantById(participantID)
                    requireNotNull(participant)
                    checkpointAndTimePairs.map { checkpointAndTime ->
                        ParticipantCheckpointTime(
                            participant = participant,
                            checkpoint = checkpointAndTime.checkpointLabel,
                            time = checkpointAndTime.time,
                        )
                    }
                }
            )
            notifyAllListeners()
        }

        fun addTimestampsFromProtocolsByCheckpoint(protocols: List<CheckpointTimestampsProtocol>) {
            timestamps.addAll(
                protocols.flatMap { (checkpoint, participantIDAndTimePairs) ->
                    participantIDAndTimePairs.map { (participantID, time) ->
                        val participant = state.participantsList.getParticipantById(participantID)
                        requireNotNull(participant)
                        ParticipantCheckpointTime(
                            participant = participant,
                            checkpoint = checkpoint,
                            time = time,
                        )
                    }
                }
            )
            notifyAllListeners()
        }

        // TODO remove use exceptions as a control flow
        fun addTimestampsFromProtocolFilesByParticipant(filePaths: List<String>) {
            val protocolsByParticipant = try {
                readAndParseAllFiles(
                    files = filePaths.map { File(it) },
                    competition = state.competition,
                    parser = ParticipantTimestampsProtocol::readFromFileContentAndCompetition,
                )
            } catch (e: ReadFailException) {
                Logger.error { e.message.toString() }
                // If something happened we don't do anything.
                return
            } catch (e: WrongFormatException) {
                Logger.error { e.message.toString() }
                // If something happened we don't do anything.
                return
            }
            addTimestampsFromProtocolsByParticipant(protocolsByParticipant)
        }

        // TODO remove use exceptions as a control flow
        fun addTimestampsFromProtocolFilesByCheckpoint(filePaths: List<String>) {
            val protocolsByCheckpoint = try {
                readAndParseAllFiles(
                    files = filePaths.map { File(it) },
                    competition = state.competition,
                    parser = CheckpointTimestampsProtocol::readFromFileContentAndCompetition,
                )
            } catch (e: ReadFailException) {
                Logger.error { e.message.toString() }
                // If something happened we don't do anything.
                return
            } catch (e: WrongFormatException) {
                Logger.error { e.message.toString() }
                // If something happened we don't do anything.
                return
            }
            addTimestampsFromProtocolsByCheckpoint(protocolsByCheckpoint)
        }

        fun clearAllTimestamps() {
            timestamps.clear()
            notifyAllListeners()
        }
    }
}