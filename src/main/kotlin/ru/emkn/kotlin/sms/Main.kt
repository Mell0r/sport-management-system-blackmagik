package ru.emkn.kotlin.sms

import org.tinylog.Logger
import ru.emkn.kotlin.sms.cli.*
import kotlinx.cli.*

/**
 * All possible program modes.
 */
enum class ProgramSubcommands {
    START,
    RESULT,
    RESULT_TEAMS,
}

fun main(args: Array<String>) {
    /*Logger.debug {"Program started."}

    Logger.debug {"Beginning to parse arguments."}
    Parsing.parse(args)
    Logger.debug {"Finished parsing arguments."}

    val configFile = getExistingReadableFileOrNull(Parsing.competitionConfigFile)
        ?: return Logger.error {"Config file \"${Parsing.competitionConfigFile}\" doesn't exist or cannot be read. Exiting."}
    Logger.debug {"Config file: $configFile"}

    val outputDirectory = ensureDirectory(Parsing.outputDirectory)
        ?: return Logger.error {"Invalid output directory \"${Parsing.outputDirectory}\". Exiting."}
    Logger.debug {"Output directory: $outputDirectory"}

    requireNotNull(Parsing.invokedSubcommand)
    when (Parsing.invokedSubcommand) {
        ProgramSubcommands.START -> {
            // Режим 1: сгенерировать стартовые протоколы и список участников
            Logger.debug { "Program command ${Parsing.invokedSubcommand}: generate start protocols and participants list." }

            val applicationFiles = Parsing.StartCommand.applicationFiles
            if (applicationFiles.isEmpty()) {
                Logger.error {"No application files were specified. Exiting."}
                return
            }

            Logger.debug {"Application files: $applicationFiles"}

            TODO("Режим 1: сгенерировать стартовые протоколы и список участников")
        }
        ProgramSubcommands.RESULT -> {
            // Режим 2: сгенерировать протоколы результатов по группам.
            Logger.debug {"Program command ${Parsing.invokedSubcommand}: generate start protocols and participants list."}

            val participantsListFile = getExistingReadableFileOrNull(Parsing.ResultCommand.participantListFile)
                ?: return Logger.error {"Participants list file \"${Parsing.ResultCommand.participantListFile}\" doesn't exist or cannot be read. Exiting."}
            val routeProtocolType = Parsing.ResultCommand.routeProtocolType
            val routeProtocolFiles = Parsing.ResultCommand.routeProtocols
            if (routeProtocolFiles.isEmpty()) {
                Logger.error {"No route protocols were specified. Exiting."}
                return
            }

            Logger.debug {"Participants list file: $participantsListFile"}
            Logger.debug {"Route protocol type: $routeProtocolType"}
            Logger.debug {"Route protocol files: $routeProtocolFiles"}

            TODO("Режим 2: сгенерировать протоколы результатов по группам.")
        }
        ProgramSubcommands.RESULT_TEAMS -> {
            // Режим 3: сгенерировать протокол командных результатов по имеющимся результата
            Logger.debug {"Program command ${Parsing.invokedSubcommand}: generate start protocols and participants list."}

            val participantsListFile = getExistingReadableFileOrNull(Parsing.ResultCommand.participantListFile)
                ?: return Logger.error {"Participants list file \"${Parsing.ResultCommand.participantListFile}\" doesn't exist or cannot be read. Exiting."}
            val resultProtocolFiles = Parsing.ResultTeamsCommand.resultProtocols
            if (resultProtocolFiles.isEmpty()) {
                Logger.error {"No result protocols were specified. Exiting."}
                return
            }

            Logger.debug {"Participants list file: $participantsListFile"}
            Logger.debug {"Route protocol files: $resultProtocolFiles"}

            TODO("Режим 3: сгенерировать протокол командных результатов по имеющимся результатам")
        }
    }
     */
}

