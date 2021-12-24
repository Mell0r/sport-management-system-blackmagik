package ru.emkn.kotlin.sms.cli

import com.github.michaelbull.result.mapBoth
import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.csv.FileContent
import ru.emkn.kotlin.sms.csv.GroupResultProtocolCsvParser
import ru.emkn.kotlin.sms.io.readAndParseAllFiles
import ru.emkn.kotlin.sms.results_processing.generateTeamResultsProtocol
import ru.emkn.kotlin.sms.successOrNothing
import java.io.File

class ResultTeamsCommand(
    val participantListFile: File,
    val resultProtocolFiles: List<File>,
) : ProgramCommand {
    override fun execute(competition: Competition, outputDirectory: File) {
        val participantsList =
            loadParticipantsList(participantListFile, competition)

        val groupResultProtocolCsvParser = GroupResultProtocolCsvParser(competition, participantsList)
        val groupResultProtocols = groupResultProtocolCsvParser.readAndParseAll(resultProtocolFiles).successOrNothing {
            Logger.error {"$it"}
            exitWithInfoLog()
        }

        val teamResultProtocol = try {
            generateTeamResultsProtocol(
                groupResultProtocols,
                participantsList
            )
        } catch (e: IllegalArgumentException) {
            Logger.error {
                "Some data needed to generate team result protocol is invalid:\n" +
                        "${e.message}"
            }
            exitWithInfoLog()
        }

        // TeamResultProtocol is CsvDumpable - thus, can trivially be saved as file
        val content = teamResultProtocol.dumpToCsv()
        val fileName = teamResultProtocol.defaultCsvFileName()
        safeWriteContentToFile(content, outputDirectory, fileName)
    }
}