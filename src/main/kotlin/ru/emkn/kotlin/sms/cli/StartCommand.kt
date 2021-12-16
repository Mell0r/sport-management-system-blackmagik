package ru.emkn.kotlin.sms.cli

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.Application
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.getStartConfigurationByApplications
import ru.emkn.kotlin.sms.io.getFileNameOfParticipantsList
import ru.emkn.kotlin.sms.io.getFileNameOfStartingProtocol
import ru.emkn.kotlin.sms.io.readAndParseAllFiles
import ru.emkn.kotlin.sms.io.safeWriteContentToFile
import java.io.File

class StartCommand(
    val applicationFiles: List<File>,
) : ProgramCommand {
    override fun execute(competition: Competition, outputDirectory: File) {
        val applications = readAndParseAllFiles(
            files = applicationFiles,
            competition = competition,
            parser = Application.Companion::readFromFileContentAndCompetition,
            strategyOnReadFail = { file ->
                // If some application couldn't be loaded as a file,
                // Then organiser has to check it manually,
                // No application should be missed due to organisers mistake.
                Logger.error { "Couldn't reach or read application file \"${file.absolutePath}\"." }
                exitWithInfoLog()
            },
            strategyOnWrongFormat = { file, exception ->
                // If some application has invalid format, we skip it.
                // It is probably team's responsibility to send a valid application???
                Logger.warn {
                    "Application at \"${file.absolutePath}\" has invalid format:\n" +
                            "${exception.message}" +
                            "Skipping this application."
                }
            },
        )

        val (participantsList, startingProtocols) = try {
            getStartConfigurationByApplications(applications, competition)
        } catch (e: IllegalArgumentException) {
            Logger.error {
                "Some data needed to generate start configuration is invalid:\n" +
                        "${e.message}"
            }
            exitWithInfoLog()
        }

        val participantsListContent = participantsList.dumpToCsv()
        val participantsListFileName =
            getFileNameOfParticipantsList(participantsList)
        val participantsListOutputFolder = File(outputDirectory, "participant-list")
        participantsListOutputFolder.mkdirs()
        safeWriteContentToFile(
            participantsListContent,
            participantsListOutputFolder,
            participantsListFileName
        )
        val startingProtocolsOutputFolder =
            File(outputDirectory, "starting-protocols")
        startingProtocolsOutputFolder.mkdirs()
        startingProtocols.forEach { startingProtocol ->
            val content = startingProtocol.dumpToCsv()
            val fileName = getFileNameOfStartingProtocol(startingProtocol)
            safeWriteContentToFile(
                content,
                startingProtocolsOutputFolder,
                fileName
            )
        }
    }
}