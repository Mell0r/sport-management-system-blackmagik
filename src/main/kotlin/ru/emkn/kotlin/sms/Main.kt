package ru.emkn.kotlin.sms

import kotlinx.cli.ExperimentalCli
import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.cli.ArgParsingSystem
import ru.emkn.kotlin.sms.io.initializeCompetition
import ru.emkn.kotlin.sms.results_processing.CheckpointTimestampsProtocol
import ru.emkn.kotlin.sms.results_processing.ParticipantTimestampsProtocol

/**
 * All possible program modes.
 */
enum class ProgramSubcommands {
    START,
    RESULT,
    RESULT_TEAMS,
}

@ExperimentalCli
fun main(args: Array<String>) {
    Logger.debug { "Program started." }

    val argParsingSystem = ArgParsingSystem()
    argParsingSystem.parse(args)
    val invokedSubcommand =
        argParsingSystem.invokedSubcommand ?: throw Exception("unreachable")
    val competitionConfig =
        initializeCompetition(argParsingSystem.competitionConfigDirectory.absolutePath)
    when (invokedSubcommand) {
        ProgramSubcommands.START -> {
            val applicationFileContents =
                argParsingSystem.startCommand.applicationFiles.map {
                    val lines = it.readLines()
                    Application.readFromFileContent(lines)
                }
            val participantListAsCsv =
                createParticipantListFromApplications(
                    applicationFileContents,
                    competitionConfig
                )
                    .dumpToCsv() // although you might do something before dumping it to csv - up to you
            val startingProtocols =
                createStartingProtocolsFromApplications(
                    applicationFileContents,
                    competitionConfig
                )
                    .map { it.dumpToCsv() }
            // save them in output directory
        }
        ProgramSubcommands.RESULT -> {
            val resultCommand = argParsingSystem.resultCommand
            val participantsList =
                ParticipantsList.readFromFileContent(resultCommand.participantListFile.readLines())
            val startingProtocols = resultCommand.startingProtocolFiles.map {
                StartingProtocol.readFromFileContent(it.readLines())
            }

            val type = resultCommand.routeProtocolType
            if (type == RouteProtocolType.OF_CHECKPOINT) {
                val checkpointTimestampsProtocols =
                    resultCommand.routeProtocolFiles.map {
                        CheckpointTimestampsProtocol.readFromFileContent(it.readLines())
                    }
                val resultProtocolsFileContents =
                    generateResultsProtocolsFromCheckpointTimestamps(
                        participantsList,
                        startingProtocols,
                        checkpointTimestampsProtocols,
                        competitionConfig
                    )
                        .map { it.dumpToCsv() }
                // save in output folder
            } else {
                val participantTimestampsProtocols =
                    resultCommand.routeProtocolFiles.map {
                        ParticipantTimestampsProtocol.readFromFileContent(it.readLines())
                    }
                val resultProtocolsFileContents =
                    generateResultsProtocolsFromParticipantTimestamps(
                        participantsList,
                        startingProtocols,
                        participantTimestampsProtocols,
                        competitionConfig
                    )
                        .map { it.dumpToCsv() }
                // save in output folder
            }

        }
        ProgramSubcommands.RESULT_TEAMS -> {
            val resultsTeamsCommand = argParsingSystem.resultTeamsCommand
            val participantsList =
                ParticipantsList.readFromFileContent(resultsTeamsCommand.participantListFile.readLines())
            val groupResultProtocols =
                resultsTeamsCommand.resultProtocolFiles.map {
                    GroupResultProtocol.readFromFileContent(
                        it.readLines()
                    )
                }
            val teamResultProtocols = generateTeamResultProtocols(
                groupResultProtocols,
                participantsList,
                competitionConfig
            )
            // TeamResultProtocol is CsvDumpable - thus, can trivially be saved as file
        }
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

fun generateResultsProtocolsFromParticipantTimestamps(
    participantsList: ParticipantsList,
    startingProtocols: List<StartingProtocol>,
    checkpointTimestampsProtocols: List<ParticipantTimestampsProtocol>,
    competitionConfig: Competition
): List<GroupResultProtocol> {
    TODO("Not yet implemented")
}

fun generateResultsProtocolsFromCheckpointTimestamps(
    participantsList: ParticipantsList,
    startingProtocols: List<StartingProtocol>,
    checkpointTimestampsProtocols: List<CheckpointTimestampsProtocol>,
    competitionConfig: Competition
): List<GroupResultProtocol> {
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


