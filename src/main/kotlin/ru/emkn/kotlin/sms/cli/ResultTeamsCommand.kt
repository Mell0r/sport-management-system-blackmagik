package ru.emkn.kotlin.sms.cli

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.GroupResultProtocol
import ru.emkn.kotlin.sms.io.getFileNameOfTeamResultsProtocol
import ru.emkn.kotlin.sms.io.readAndParseAllFiles
import ru.emkn.kotlin.sms.io.safeWriteContentToFile
import ru.emkn.kotlin.sms.results_processing.generateTeamResultsProtocol
import java.io.File

class ResultTeamsCommand(
    val participantListFile: File,
    val resultProtocolFiles: List<File>,
) : ProgramCommand() {
    override fun execute(competition: Competition, outputDirectory: File) {
        val participantsList =
            loadParticipantsList(participantListFile, competition)

        // All group result protocols must be valid and readable
        val groupResultProtocols = readAndParseAllFiles(
            files = resultProtocolFiles,
            competition = competition,
            parser = GroupResultProtocol.Companion::readFromFileContentAndCompetition,
            strategyOnReadFail = { file ->
                Logger.error { "Group result protocol at \"${file.absolutePath}\" couldn't be reached or read." }
                exitWithInfoLog()
            },
            strategyOnWrongFormat = { file, exception ->
                Logger.error {
                    "Group result protocol at \"${file.absolutePath}\" has invalid format:\n" +
                            "${exception.message}"
                }
                exitWithInfoLog()
            },
        )

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
        val fileName = getFileNameOfTeamResultsProtocol(teamResultProtocol)
        safeWriteContentToFile(content, outputDirectory, fileName)
    }
}