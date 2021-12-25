package ru.emkn.kotlin.sms.cli

import com.github.michaelbull.result.mapBoth
import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.ParticipantsList
import ru.emkn.kotlin.sms.csv.ParticipantsListCsvParser
import ru.emkn.kotlin.sms.io.ensureDirectory
import ru.emkn.kotlin.sms.io.initializeCompetition
import ru.emkn.kotlin.sms.successOrNothing
import java.io.File
import kotlin.system.exitProcess

@Suppress("unused") // as it is a possible mode
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
    return initializeCompetition(competitionConfigDirPath).mapBoth(
        success = { it },
        failure = { message ->
            Logger.error {
                "Competition config files in directory \"$competitionConfigDirPath\" are invalid! See following exception:\n" +
                        "$message"
            }
            exitWithInfoLog()
        },
    )
}

private fun ensureOutputDirectory(outputDirectoryPath: String): File {
    // Output directory MUST be loaded, otherwise program has to terminate.
    return ensureDirectory(outputDirectoryPath).successOrNothing { eMessage ->
        Logger.error {
            "Couldn't initialize output directory \"${outputDirectoryPath}\". " +
                    "Following exception occurred: $eMessage."
        }
        exitWithInfoLog()
    }
}

fun loadParticipantsList(
    participantListFile: File,
    competition: Competition
): ParticipantsList {
    return ParticipantsListCsvParser(competition).readAndParse(
        participantListFile
    ).successOrNothing {
        Logger.error {
            "Could not load participants list at \"${participantListFile.absolutePath}\":\n$it"
        }
        exitWithInfoLog()
    }
}