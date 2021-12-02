package ru.emkn.kotlin.sms.cli

import kotlinx.cli.*
import org.tinylog.Logger
import ru.emkn.kotlin.sms.DEFAULT_ROUTE_PROTOCOL_TYPE
import ru.emkn.kotlin.sms.ProgramSubcommands
import ru.emkn.kotlin.sms.RouteProtocolType
import java.io.File
import kotlin.system.exitProcess


/**
 * A set of [kotlinx.cli] objects, such as [ArgParser], arguments, options
 * and subcommands, suitable for parsing command-line arguments for this program.
 */
class ArgParsingSystem {
    val argParser = ArgParser("java -jar sms.jar")

    /**
     * After parsing contains a subcommand chosen by user.
     *
     * May equal to null only when no subcommand was specified
     * (but all mandatory arguments like [competitionConfigDirectory] were specified).
     */
    var invokedSubcommand: ProgramSubcommands? = null

    val competitionConfigDirectory by argParser.argument(
        FileArgType,
        description = "Competition config directory",
    )

    val outputDirectory by argParser.option(
        FileArgType,
        shortName = "o",
        fullName = "output",
        description = "Output directory",
    ).default(File("."))


    /**
     * Mode 1 of the program (called "start")
     */
    inner class StartCommand : Subcommand(
        "start",
        "Given team applications, generates starting protocols and participants list."
    ) {
        val applicationFiles by option(
            CsvFileListArgType,
            shortName = "a",
            fullName = "applications",
            description = "A list of team application files (in .csv) or directories, separated by commas.",
        ).required()

        override fun execute() {
            invokedSubcommand = ProgramSubcommands.START
        }
    }

    val startCommand = StartCommand()


    /**
     * Mode 2 of the program (called "result")
     */
    inner class ResultCommand : Subcommand(
        "result",
        "Given participants list, starting protocols and route completion protocols, generates result protocols (by groups)."
    ) {
        val participantListFile by option(
            FileArgType,
            shortName = "p",
            fullName = "participants",
            description = "Participants list file",
        ).required()

        val startingProtocolFiles by option(
            CsvFileListArgType,
            shortName = "s",
            fullName = "startingProtocols",
            description = "A list of starting protocol files (in .csv) or directories, separated by commas."
        ).required()

        val routeProtocolType by option(
            ArgType.Choice<RouteProtocolType>(),
            shortName = "tp",
            fullName = "routeProtocolType",
            description = "Route protocol type",
        ).default(DEFAULT_ROUTE_PROTOCOL_TYPE)

        val routeProtocolFiles by option(
            CsvFileListArgType,
            shortName = "r",
            fullName = "routeProtocols",
            description = "A list of route protocol files (in .csv) or directories, separated by commas.",
        ).required()

        override fun execute() {
            invokedSubcommand = ProgramSubcommands.RESULT
        }
    }

    val resultCommand = ResultCommand()


    /**
     * Mode 3 of the program (called "result_teams")
     */
    inner class ResultTeamsCommand : Subcommand(
        "result_teams",
        "Given result protocols (by groups) and participants list generates team result protocols."
    ) {
        val participantListFile by option(
            FileArgType,
            shortName = "p",
            fullName = "participants",
            description = "Participants list file",
        ).required()

        val resultProtocolFiles by option(
            CsvFileListArgType,
            shortName = "r",
            fullName = "resultProtocols",
            description = "A list of result protocol files (in .csv) or directories, separated by commas.",
        ).required()

        override fun execute() {
            invokedSubcommand = ProgramSubcommands.RESULT_TEAMS
        }
    }

    val resultTeamsCommand = ResultTeamsCommand()

    init {
        argParser.subcommands(startCommand, resultCommand, resultTeamsCommand)
    }

    fun parse(args: Array<String>) {
        Logger.debug { "Beginning to parse arguments." }

        argParser.parse(args)

        // Currently, kotlinx-cli doesn't handle the case, where no subcommand is given
        // So we need to check for it manually
        if (invokedSubcommand == null) {
            Logger.error {
                "No subcommand is given.\n" +
                        "Please specify one of the subcommands: \"start\", \"result\", \"result_teams\";\n" +
                        "Or use -h (--help) to see help message."
            }
            exitProcess(255)
        }

        Logger.debug { "Successfully finished parsing arguments." }
    }
}
