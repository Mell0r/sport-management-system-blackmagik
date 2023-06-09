@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_OVERRIDE")

package ru.emkn.kotlin.sms.cli

import kotlinx.cli.*
import org.tinylog.Logger
import ru.emkn.kotlin.sms.RouteProtocolType
import java.io.File
import kotlin.system.exitProcess


/**
 * A set of [kotlinx.cli] objects, such as [ArgParser], arguments, options
 * and subcommands, suitable for parsing command-line arguments for this program.
 */
class ArgParsingSystem {
    private val argParser = ArgParser("java -jar sms.jar")

    /**
     * After parsing contains a subcommand chosen by user.
     *
     * May equal to null only when no subcommand was specified
     * (but all mandatory arguments like [competitionConfigDirectory] were specified).
     */
    var invokedCommand: ProgramCommand? = null

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
    inner class StartSubcommand : Subcommand(
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
            invokedCommand = StartCommand(
                applicationFiles = applicationFiles,
            )
        }
    }

    val startSubcommand = StartSubcommand()


    /**
     * Mode 2 of the program (called "result")
     */
    inner class ResultSubcommand : Subcommand(
        "result",
        "Given participants list, starting protocols and route completion protocols, generates result protocols (by groups)."
    ) {
        val participantListFile by option(
            FileArgType,
            shortName = "p",
            fullName = "participants",
            description = "Participants list file",
        ).required()

        val routeProtocolType by option(
            ArgType.Choice<RouteProtocolType>(),
            shortName = "tp",
            fullName = "routeProtocolType",
            description = "Route protocol type",
        ).default(RouteProtocolType.DEFAULT)

        val routeProtocolFiles by option(
            CsvFileListArgType,
            shortName = "r",
            fullName = "routeProtocols",
            description = "A list of route protocol files (in .csv) or directories, separated by commas.",
        ).required()

        override fun execute() {
            invokedCommand = ResultCommand(
                participantListFile = participantListFile,
                routeProtocolType = routeProtocolType,
                routeProtocolFiles = routeProtocolFiles,
            )
        }
    }

    val resultSubcommand = ResultSubcommand()


    /**
     * Mode 3 of the program (called "result_teams")
     */
    inner class ResultTeamsSubcommand : Subcommand(
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
            invokedCommand = ResultTeamsCommand(
                participantListFile = participantListFile,
                resultProtocolFiles = resultProtocolFiles,
            )
        }
    }

    val resultTeamsSubcommand = ResultTeamsSubcommand()

    init {
        argParser.subcommands(
            startSubcommand,
            resultSubcommand,
            resultTeamsSubcommand
        )
    }

    fun parse(args: Array<String>) {
        Logger.debug { "Beginning to parse arguments." }

        argParser.parse(args)

        // Currently, kotlinx-cli doesn't handle the case, where no subcommand is given
        // So we need to check for it manually
        if (invokedCommand == null) {
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
