package ru.emkn.kotlin.sms.cli

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.RouteProtocolType
import ru.emkn.kotlin.sms.io.readAndParseAllFiles
import ru.emkn.kotlin.sms.results_processing.*
import java.io.File

class ResultCommand(
    val participantListFile: File,
    val routeProtocolType: RouteProtocolType,
    val routeProtocolFiles: List<File>,
) : ProgramCommand {
    override fun execute(competition: Competition, outputDirectory: File) {
        val participantsList =
            loadParticipantsList(participantListFile, competition)

        fun routeCompletionProtocolReadFailStrategy(file: File) {
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
                val fileName = protocol.defaultCsvFileName()
                safeWriteContentToFile(content, outputDirectory, fileName)
            }
        }

        val timestampsProtocolProcessor = TimestampsProtocolProcessor(participantsList)

        val timestamps = if (routeProtocolType == RouteProtocolType.OF_CHECKPOINT) {
            val checkpointTimestampsProtocols = readAndParseAllFiles(
                files = routeProtocolFiles,
                competition = competition,
                parser = CheckpointTimestampsProtocol.Companion::readFromCsvContentAndCompetition,
                strategyOnReadFail = ::routeCompletionProtocolReadFailStrategy,
                strategyOnWrongFormat = ::routeCompletionProtocolHasWrongFormatStrategy,
            )
            timestampsProtocolProcessor.processByCheckpoint(checkpointTimestampsProtocols)
        } else {
            val participantTimestampsProtocols = readAndParseAllFiles(
                files = routeProtocolFiles,
                competition = competition,
                parser = ParticipantTimestampsProtocol.Companion::readFromCsvContentAndCompetition,
                strategyOnReadFail = ::routeCompletionProtocolReadFailStrategy,
                strategyOnWrongFormat = ::routeCompletionProtocolHasWrongFormatStrategy,
            )
            timestampsProtocolProcessor.processByParticipant(participantTimestampsProtocols)
        }

        val groupResultProtocols = GroupResultProtocolGenerator(participantsList).generate(timestamps)
        saveGroupResultProtocols(groupResultProtocols)
    }
}