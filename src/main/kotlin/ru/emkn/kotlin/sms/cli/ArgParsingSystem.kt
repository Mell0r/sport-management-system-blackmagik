package ru.emkn.kotlin.sms.cli

import kotlinx.cli.*
import ru.emkn.kotlin.sms.*
import java.io.File
import org.tinylog.Logger
import kotlin.system.*

class ArgParsingSystem {
    val argParser = ArgParser("java -jar sms.jar")
    var invokedSubcommand: ProgramSubcommands? = null

    val competitionConfigFile by argParser.argument(
        FileArgType,
        description = "Competition config file (in .json)",
    )
    val outputDirectory by argParser.option(
        FileArgType,
        shortName = "o",
        fullName = "output",
        description = "Output directory",
    ).default(File("."))

    inner class StartCommand : Subcommand(
        "start",
        "Given team applications, generates start protocols and participants list."
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

    inner class ResultCommand : Subcommand(
        "result",
        "Given participants list and route completion protocols, generates result protocols (by groups)."
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
        Logger.debug {"Beginning to parse arguments."}

        argParser.parse(args)

        // Currently, kotlinx-cli doesn't handle the case, where no subcommand is given
        // So we need to check for it manually
        if (invokedSubcommand == null) {
            Logger.error {"No subcommand is given.\n" +
                    "Please specify one of the subcommands: \"start\", \"result\", \"result_teams\";\n" +
                    "Or use -h (--help) to see help message."}
            exitProcess(255)
        }

        Logger.debug {"Successfully finished parsing arguments."}
    }
}
