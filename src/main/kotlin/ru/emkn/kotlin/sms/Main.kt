package ru.emkn.kotlin.sms

import kotlinx.cli.ExperimentalCli
import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.cli.ArgParsingSystem
import ru.emkn.kotlin.sms.io.*
import ru.emkn.kotlin.sms.results_processing.CheckpointTimestampsProtocol
import ru.emkn.kotlin.sms.results_processing.ParticipantTimestampsProtocol
import ru.emkn.kotlin.sms.results_processing.generateResultsProtocolsFromCheckpointTimestamps
import ru.emkn.kotlin.sms.results_processing.generateResultsProtocolsFromParticipantTimestamps
import java.io.File
import kotlin.system.exitProcess

/**
 * All possible program modes.
 */
enum class ProgramSubcommands {
    START,
    RESULT,
    RESULT_TEAMS,
}

fun exitWithInfoLog() : Nothing {
    Logger.info {"Terminating..."}
    exitProcess(255)
}




private fun loadCompetition(competitionConfigDirPath: String) : Competition {
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


private fun ensureOutputDirectory(outputDirectoryPath: String) : File {
    // Output directory MUST be loaded, otherwise program has to terminate.
    return try {
        ensureDirectory(outputDirectoryPath)
    } catch (e: Exception) {
        Logger.error {"Couldn't initialize output directory \"${outputDirectoryPath}\". Following exception occurred:"}
        exitWithInfoLog()
    }
}


private fun start(
    startCommand: ArgParsingSystem.StartCommand,
    competition: Competition,
    outputDirectory: File,
) {
    val applications = readAndParseAllFiles(
        files = startCommand.applicationFiles,
        parser = Application::readFromFileContent,
        strategyIfCouldntRead = { file ->
            // If some application couldn't be loaded as a file,
            // Then organiser has to check it manually,
            // No application should be missed due to organisers mistake.
            Logger.error {"Couldn't reach or read application file \"${file.absolutePath}\"."}
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

    val participantListAsCsv = createParticipantListFromApplications(
        applications,
        competition
    ).dumpToCsv() // although you might do something before dumping it to csv - up to you
    val startingProtocolsAsCsv =
        createStartingProtocolsFromApplications(
            applications,
            competition
        )
            .map { it.dumpToCsv() }
    // save them in output directory
}


private fun loadParticipantsList(participantListFile: File) : ParticipantsList =
    readAndParseFile(
        file = participantListFile,
        parser = ParticipantsList::readFromFileContent,
        strategyIfCouldntRead = { file ->
            // Participants list MUST be readable
            // Otherwise, terminate
            Logger.error {"Couldn't read participants list at \"${file.path}\"."}
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


private fun result(
    resultCommand: ArgParsingSystem.ResultCommand,
    competition: Competition,
    outputDirectory: File,
){
    val participantsList = loadParticipantsList(resultCommand.participantListFile)

    val startingProtocols = readAndParseAllFiles(
        files = resultCommand.startingProtocolFiles,
        parser = StartingProtocol::readFromFileContent,
        strategyIfCouldntRead = { file ->
            // Starting protocol MUST be read
            // Otherwise terminating
            Logger.error {"Starting protocol at \"${file.absolutePath}\" cannot be reached or read."}
            exitWithInfoLog()
        },
        strategyOnWrongFormat = { file, exception ->
            // Starting protocol MUST have correct format
            // Otherwise terminating
            Logger.error {
                "Starting protocol at \"${file.absolutePath}\" has invalid format:\n" +
                        "${exception.message}"
            }
        },
    )

    fun routeCompletionProtocolCouldntBeReadStrategy(file: File) {
        Logger.error {"Route completion protocol at \"${file.absolutePath}\" cannot be reached or read"}
        exitWithInfoLog()
    }
    fun routeCompletionProtocolHasWrongFormatStrategy(file: File, exception: IllegalArgumentException) {
        Logger.error {
            "Route completion protocol at \"${file.absolutePath}\" has wrong format:\n" +
                    "${exception.message}"
        }
        exitWithInfoLog()
    }

    val type = resultCommand.routeProtocolType
    if (type == RouteProtocolType.OF_CHECKPOINT) {
        val checkpointTimestampsProtocols = readAndParseAllFiles(
            files = resultCommand.routeProtocolFiles,
            parser = CheckpointTimestampsProtocol::readFromFileContent,
            strategyIfCouldntRead = ::routeCompletionProtocolCouldntBeReadStrategy,
            strategyOnWrongFormat = ::routeCompletionProtocolHasWrongFormatStrategy,
        )

        val resultProtocolsFileContents =
            generateResultsProtocolsFromCheckpointTimestamps(
                participantsList,
                startingProtocols,
                checkpointTimestampsProtocols,
                competition
            )
                .map { it.dumpToCsv() }
        // save in output folder
    } else {
        val participantTimestampsProtocols = readAndParseAllFiles(
            files = resultCommand.routeProtocolFiles,
            parser = ParticipantTimestampsProtocol::readFromFileContent,
            strategyIfCouldntRead = ::routeCompletionProtocolCouldntBeReadStrategy,
            strategyOnWrongFormat = ::routeCompletionProtocolHasWrongFormatStrategy,
        )

        val resultProtocolsFileContents =
            generateResultsProtocolsFromParticipantTimestamps(
                participantsList,
                startingProtocols,
                participantTimestampsProtocols,
                competition
            )
                .map { it.dumpToCsv() }
        // save in output folder
    }

}

private fun resultTeams(
    resultsTeamsCommand: ArgParsingSystem.ResultTeamsCommand,
    competition: Competition,
    outputDirectory: File,
)
{
    val participantsList = loadParticipantsList(resultsTeamsCommand.participantListFile)

    val groupResultProtocols = readAndParseAllFiles(
        files = resultsTeamsCommand.resultProtocolFiles,
        parser = GroupResultProtocol::readFromFileContent,
        strategyIfCouldntRead = { file ->
            Logger.error {"Group result protocol at \"${file.absolutePath}\" couldn't be reached or read."}
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

    val teamResultProtocols = generateTeamResultProtocols(
        groupResultProtocols,
        participantsList,
        competition
    )
    // TeamResultProtocol is CsvDumpable - thus, can trivially be saved as file
}

@ExperimentalCli
fun main(args: Array<String>) {
    Logger.debug { "Program started." }

    val argParsingSystem = ArgParsingSystem()
    argParsingSystem.parse(args)

    val invokedSubcommand = argParsingSystem.invokedSubcommand
        ?: return Logger.error {"No subcommand specified. Terminating."}
    Logger.debug {"Invoked subcommand: $invokedSubcommand."}

    val competition = loadCompetition(argParsingSystem.competitionConfigDirectory.absolutePath)
    val outputDirectory = ensureOutputDirectory(argParsingSystem.outputDirectory.absolutePath)

    when (invokedSubcommand) {
        ProgramSubcommands.START -> start(argParsingSystem.startCommand, competition, outputDirectory)
        ProgramSubcommands.RESULT -> result(argParsingSystem.resultCommand, competition, outputDirectory)
        ProgramSubcommands.RESULT_TEAMS -> resultTeams(argParsingSystem.resultTeamsCommand, competition, outputDirectory)
    }

    Logger.debug { "Program successfully finished." }
}

// all the functions below should be NOT in this file, but in respective module.
// competition parameter is everywhere just in case; worst case scenario:
// it will be deleted as unused parameter later.
fun generateTeamResultProtocols(
    groupResultProtocols: List<GroupResultProtocol>,
    participantsList: ParticipantsList,
    competitionConfig: Competition
): List<TeamResultsProtocol> {
    TODO("Not yet implemented")
}

// should be in a respective package (definitely not this file)
fun createStartingProtocolsFromApplications(
    applicationFileContents: List<Application>,
    competitionConfig: Competition
): List<StartingProtocol> {
    TODO("Not yet implemented")
}

// should be in a respective package (definitely not this file)
fun createParticipantListFromApplications(
    applicationFileContents: List<Application>,
    competitionConfig: Competition
): ParticipantsList {
    TODO("Not yet implemented")
}


