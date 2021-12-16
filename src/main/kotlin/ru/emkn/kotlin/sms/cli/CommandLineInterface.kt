package ru.emkn.kotlin.sms.cli

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.io.*
import java.io.File
import kotlin.system.exitProcess

fun runCommandLineInterface(args: Array<String>) {
    val argParsingSystem = ArgParsingSystem()
    argParsingSystem.parse(args)

    val invokedSubcommand = argParsingSystem.invokedCommand
        ?: return Logger.error { "No subcommand specified. Terminating." }
    Logger.debug { "Invoked subcommand: ${invokedSubcommand.javaClass}." }

    val competition =
        loadCompetition(argParsingSystem.competitionConfigDirectory.absolutePath)
    val outputDirectory =
        ensureOutputDirectory(argParsingSystem.outputDirectory.absolutePath)

    invokedSubcommand.execute(
        competition,
        outputDirectory,
    )
}

fun exitWithInfoLog(): Nothing {
    Logger.info { "Terminating..." }
    exitProcess(255)
}

private fun loadCompetition(competitionConfigDirPath: String): Competition {
    // Competition config MUST be loaded, otherwise program has to terminate.
    return try {
        initializeCompetition(competitionConfigDirPath)
    } catch (e: IllegalArgumentException) {
        Logger.error {
            "Competition config files in directory \"$competitionConfigDirPath\" are invalid! See following exception:\n" +
                    "${e.message}"
        }
        exitWithInfoLog()
    }
}

private fun ensureOutputDirectory(outputDirectoryPath: String): File {
    // Output directory MUST be loaded, otherwise program has to terminate.
    return try {
        ensureDirectory(outputDirectoryPath)
    } catch (e: Exception) {
        Logger.error { "Couldn't initialize output directory \"${outputDirectoryPath}\". Following exception occurred:" }
        exitWithInfoLog()
    }
}

fun loadParticipantsList(participantListFile: File, competition: Competition): ParticipantsList =
    readAndParseFile(
        file = participantListFile,
        competition = competition,
        parser = ParticipantsList.Companion::readFromFileContentAndCompetition,
        strategyOnReadFail = { file ->
            // Participants list MUST be readable
            // Otherwise, terminate
            Logger.error { "Couldn't read participants list at \"${file.path}\"." }
            exitWithInfoLog()
        },
        strategyOnWrongFormat = { file, exception ->
            // Participants list MUST have correct format
            // Otherwise, terminate
            Logger.error {
                "Participants list at \"${file.path}\" has invalid format:\n" +
                        "${exception.message}"
            }
            exitWithInfoLog()
        },
    )