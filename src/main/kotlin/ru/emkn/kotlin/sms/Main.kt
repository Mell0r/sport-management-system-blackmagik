package ru.emkn.kotlin.sms

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.cli.*
import ru.emkn.kotlin.sms.io.*
import ru.emkn.kotlin.sms.results_processing.*
import kotlin.system.exitProcess

/**
 * All possible program modes.
 */
enum class ProgramSubcommands {
    START,
    RESULT,
    RESULT_TEAMS,
}

fun main(args: Array<String>) {
    Logger.debug { "Program started." }

    val argParsingSystem = ArgParsingSystem()
    argParsingSystem.parse(args)

    //val competition: Competition = TODO()

    val outputDirectory = ensureDirectory(argParsingSystem.outputDirectory.absolutePath) ?: {
        Logger.error {"Couldn't set up output directory. Terminating."}
        exitProcess(-1)
    }

    when (argParsingSystem.invokedSubcommand) {
        ProgramSubcommands.START -> {
            val applications = readAllReadableFiles(argParsingSystem.startCommand.applicationFiles) {
                Logger.warn {"Couldn't reach or read application \"$it\"."}
            }
            TODO()
        }
        ProgramSubcommands.RESULT -> {
            val startProtocols = readAllReadableFiles(argParsingSystem.resultCommand.startingProtocolFiles) {
                Logger.warn {"Couldn't reach or read starting protocol \"$it\"."}
            }
            val completionProtocols = readAllReadableFiles(argParsingSystem.resultCommand.routeProtocolFiles) {
                Logger.warn {"Couldn't reach or read route completion protocol \"$it\"."}
            }
            val fromStartProtocols = FromStartProtocols(startProtocols)
            val resultFiles = if (argParsingSystem.resultCommand.routeProtocolType == RouteProtocolType.OF_PARTICIPANT) {
                TODO()
            } else { // OF_CHECKPOINT
                TODO()
            }

        }
        ProgramSubcommands.RESULT_TEAMS -> TODO()
        null -> {
            Logger.error {"No command was invoked. Terminating."}
            exitProcess(-1)
        }
    }

    Logger.debug { "Program successfully finished." }
}