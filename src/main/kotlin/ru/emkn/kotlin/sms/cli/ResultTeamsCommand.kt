package ru.emkn.kotlin.sms.cli

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.csv.GroupResultProtocolCsvParser
import ru.emkn.kotlin.sms.results_processing.SampleTeamResultsCalculator
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

        val teamResultProtocol = SampleTeamResultsCalculator.calculate(groupResultProtocols)

        // TeamResultProtocol is CsvDumpable - thus, can trivially be saved as file
        val content = teamResultProtocol.dumpToCsv()
        val fileName = teamResultProtocol.defaultCsvFileName()
        safeWriteContentToFile(content, outputDirectory, fileName)
    }
}