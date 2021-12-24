package ru.emkn.kotlin.sms.cli

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.RouteProtocolType
import ru.emkn.kotlin.sms.results_processing.*
import ru.emkn.kotlin.sms.successOrNothing
import java.io.File

class ResultCommand(
    val participantListFile: File,
    val routeProtocolType: RouteProtocolType,
    val routeProtocolFiles: List<File>,
) : ProgramCommand {
    override fun execute(competition: Competition, outputDirectory: File) {
        val participantsList =
            loadParticipantsList(participantListFile, competition)

        fun routeCompletionProtocolReadParseFailStrategy(eMessage: String?): Nothing {
            Logger.error("Could not read or parse route completion protocols:\n$eMessage")
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
            val checkpointTimestampsProtocols = CheckpointTimestampsProtocol
                .readAndParseAll(routeProtocolFiles)
                .successOrNothing(::routeCompletionProtocolReadParseFailStrategy)
            timestampsProtocolProcessor.processByCheckpoint(checkpointTimestampsProtocols)
        } else {
            val participantTimestampsProtocols = ParticipantTimestampsProtocol
                .readAndParseAll(routeProtocolFiles)
                .successOrNothing(::routeCompletionProtocolReadParseFailStrategy)
            timestampsProtocolProcessor.processByParticipant(participantTimestampsProtocols)
        }

        val groupResultProtocols = GroupResultProtocolGenerator(participantsList).generate(timestamps)
        saveGroupResultProtocols(groupResultProtocols)
    }
}