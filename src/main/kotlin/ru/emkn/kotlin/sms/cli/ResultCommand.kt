package ru.emkn.kotlin.sms.cli

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.GroupResultProtocol
import ru.emkn.kotlin.sms.RouteProtocolType
import ru.emkn.kotlin.sms.StartingProtocol
import ru.emkn.kotlin.sms.io.getFileNameOfGroupResultProtocol
import ru.emkn.kotlin.sms.io.readAndParseAllFiles
import ru.emkn.kotlin.sms.io.safeWriteContentToFile
import ru.emkn.kotlin.sms.results_processing.CheckpointTimestampsProtocol
import ru.emkn.kotlin.sms.results_processing.ParticipantTimestampsProtocol
import ru.emkn.kotlin.sms.results_processing.generateResultsProtocolsOfCheckpoint
import ru.emkn.kotlin.sms.results_processing.generateResultsProtocolsOfParticipant
import java.io.File

class ResultCommand(
    val participantListFile: File,
    val startingProtocolFiles: List<File>,
    val routeProtocolType: RouteProtocolType,
    val routeProtocolFiles: List<File>,
) : ProgramCommand() {
    override fun execute(competition: Competition, outputDirectory: File) {
        val participantsList =
            loadParticipantsList(participantListFile, competition)

        val startingProtocols = readAndParseAllFiles(
            files = startingProtocolFiles,
            competition = competition,
            parser = StartingProtocol.Companion::readFromFileContentAndCompetition,
            strategyOnReadFail = { file ->
                // Starting protocol MUST be read
                // Otherwise terminating
                Logger.error { "Starting protocol at \"${file.absolutePath}\" cannot be reached or read." }
                exitWithInfoLog()
            },
            strategyOnWrongFormat = { file, exception ->
                // Starting protocol MUST have correct format
                // Otherwise terminating
                Logger.error {
                    "Starting protocol at \"${file.absolutePath}\" has invalid format:\n" +
                            "${exception.message}"
                }
                exitWithInfoLog()
            },
        )

        fun routeCompletionProtocolCouldntBeReadStrategy(file: File) {
            Logger.error { "Route completion protocol at \"${file.absolutePath}\" cannot be reached or read" }
            exitWithInfoLog()
        }

        fun routeCompletionProtocolHasWrongFormatStrategy(
            file: File,
            exception: IllegalArgumentException
        ) {
            Logger.error {
                "Route completion protocol at \"${file.absolutePath}\" has wrong format:\n" +
                        "${exception.message}"
            }
            exitWithInfoLog()
        }

        fun saveGroupResultProtocols(protocols: List<GroupResultProtocol>) {
            protocols.forEach { protocol ->
                val content = protocol.dumpToCsv()
                val fileName = getFileNameOfGroupResultProtocol(protocol)
                safeWriteContentToFile(content, outputDirectory, fileName)
            }
        }

        val type = routeProtocolType

        if (type == RouteProtocolType.OF_CHECKPOINT) {
            val checkpointTimestampsProtocols = readAndParseAllFiles(
                files = routeProtocolFiles,
                competition = competition,
                parser = CheckpointTimestampsProtocol.Companion::readFromFileContentAndCompetition,
                strategyOnReadFail = ::routeCompletionProtocolCouldntBeReadStrategy,
                strategyOnWrongFormat = ::routeCompletionProtocolHasWrongFormatStrategy,
            )

            val resultProtocols = try {
                generateResultsProtocolsOfCheckpoint(
                    participantsList,
                    startingProtocols,
                    checkpointTimestampsProtocols,
                    competition
                )
            } catch (e: IllegalArgumentException) {
                Logger.error {
                    "Some data needed to generate group result protocols is invalid:\n" +
                            "${e.message}"
                }
                exitWithInfoLog()
            }

            saveGroupResultProtocols(resultProtocols)
        } else {
            val participantTimestampsProtocols = readAndParseAllFiles(
                files = routeProtocolFiles,
                competition = competition,
                parser = ParticipantTimestampsProtocol.Companion::readFromFileContentAndCompetition,
                strategyOnReadFail = ::routeCompletionProtocolCouldntBeReadStrategy,
                strategyOnWrongFormat = ::routeCompletionProtocolHasWrongFormatStrategy,
            )

            val resultProtocols = try {
                generateResultsProtocolsOfParticipant(
                    participantsList,
                    startingProtocols,
                    participantTimestampsProtocols,
                    competition
                )
            } catch (e: IllegalArgumentException) {
                Logger.error {
                    "Some data needed to generate result protocols is invalid:\n" +
                            "${e.message}"
                }
                exitWithInfoLog()
            }

            // save in output folder
            saveGroupResultProtocols(resultProtocols)
        }
    }
}