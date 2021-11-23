package ru.emkn.kotlin.sms

import org.tinylog.Logger
import kotlinx.cli.*
import java.io.File
import java.nio.file.Files.createDirectories

private fun createAncestorDirectories(file: File) {
    val parentPath = file.absoluteFile.parentFile.toPath()
    createDirectories(parentPath)
}

private fun ensureFile(file: File) {
    createAncestorDirectories(file)
    file.createNewFile()
}

/**
 * Creates a directory by given filepath and all it's ancestors.
 * If the specified file is not a directory, and IllegalArgumentException is thrown.
 */
private fun ensureDirectory(directoryPath: String) : File {
    val file = File(directoryPath)
    require(file.isDirectory) { "\"$directoryPath\" is not a directory." }
    ensureFile(file)
    return file
}

/**
 * Returns java.io.File by it's name and checks that it exists and can be read.
 */
private fun getExistingReadableFile(fileName: String) : File {
    val file = File(fileName)
    require(file.exists() && file.canRead()) {
        "File \"${file.absolutePath}\" doesn't exist or cannot be read."
    }
    return file
}


/**
 * Given a list of file names and directory names,
 * returns all .csv files which are in the list
 * or in some directory in the list.
 */
fun filterCsvFilesFromFilesAndDirs(filenames: List<String>) : List<File> {
    val files = filenames.map { File(it) }
    val directories = files.filter { it.isDirectory }
    val filesInDirectories = directories
        .flatMap { it.walk() }
    val allFiles = (files + filesInDirectories).distinct()
    val (csvFiles, otherFiles) = allFiles.partition { it.extension == "csv" }
    otherFiles.forEach { file ->
        Logger.info {"File $file does not have .csv extension. Skipping."}
    }
    return csvFiles
}


/**
 * All possible program modes.
 */
enum class ProgramSubcommands {
    START,
    RESULT,
    RESULT_TEAMS,
}

object Parsing {
    val argParser = ArgParser("java -jar sms.jar")
    var invokedSubcommand: ProgramSubcommands? = null

    val competitionConfigFileName by argParser.argument(
        ArgType.String,
        description = "Competition config file (in .json)",
    )
    val output by StartCommand.option(
        ArgType.String,
        shortName = "o",
        description = "Output directory (for participants list and start protocols)",
    ).default(".")

    object StartCommand : Subcommand(
        "start",
        "Given team applications, generates start protocols and participants list."
    ) {
        val applications by option(
            ArgType.String,
            shortName = "a",
            description = "A list of team application files (in .csv) or directories, separated by commas.",
        ).required().delimiter(",")

        override fun execute() {
            invokedSubcommand = ProgramSubcommands.START
        }
    }

    object ResultCommand : Subcommand(
        "result",
        "Given participants list and route completion protocols, generates result protocols (by groups)."
    ) {
        val participants by option(
            ArgType.String,
            shortName = "p",
            description = "Participants list",
        ).required()

        val routeProtocolType by option(
            ArgType.Choice<RouteProtocolType>(),
            shortName = "type",
            description = "Route protocol type",
        ).default(DEFAULT_ROUTE_PROTOCOL_TYPE)

        val routeProtocols by option(
            ArgType.String,
            shortName = "r",
            description = "A list of route protocol files (in .csv) or directories, separated by commas.",
        ).required().delimiter(",")

        override fun execute() {
            invokedSubcommand = ProgramSubcommands.RESULT
        }
    }

    object ResultTeamsCommand : Subcommand(
        "result_teams",
        "Given result protocols (by groups) and participants list generates team result protocols."
    ) {
        val participants by option(
            ArgType.String,
            shortName = "p",
            description = "Participants list",
        ).required()

        val resultProtocols by option(
            ArgType.String,
            shortName = "r",
            description = "A list of result protocol files (in .csv) or directories, separated by commas.",
        ).required().delimiter(",")

        override fun execute() {
            invokedSubcommand = ProgramSubcommands.RESULT_TEAMS
        }
    }

    init {
        argParser.subcommands(StartCommand, ResultCommand, ResultTeamsCommand)
    }

    fun parse(args: Array<String>) {
        argParser.parse(args)
    }
}

fun main(args: Array<String>) {
    Logger.debug {"Program started."}

    Logger.debug {"Beginning to parse arguments."}
    Parsing.parse(args)
    Logger.debug {"Finished parsing arguments."}

    val configFile = try {
        getExistingReadableFile(Parsing.competitionConfigFileName)
    } catch (e: Exception) {
        Logger.error {"Couldn't find config file. A following exception occured."}
        Logger.error(e)
        return
    }
    Logger.debug {"Config file: $configFile"}

    val outputDirectory = try {
        ensureDirectory(Parsing.output)
    } catch (e: Exception) {
        Logger.error(e)
        return
    }
    Logger.debug {"Output directory: $outputDirectory"}

    requireNotNull(Parsing.invokedSubcommand)
    when (Parsing.invokedSubcommand) {
        ProgramSubcommands.START -> {
            // Режим 1: сгенерировать стартовые протоколы и список участников
            Logger.debug { "Program command ${Parsing.invokedSubcommand}: generate start protocols and participants list." }

            val applicationFiles = filterCsvFilesFromFilesAndDirs(Parsing.StartCommand.applications)
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

            val participantsListFile = try {
                getExistingReadableFile(Parsing.ResultCommand.participants)
            } catch (e: Exception) {
                Logger.error {"Couldn't find participants list file. A following exception."}
                Logger.error(e)
                return
            }
            val routeProtocolType = Parsing.ResultCommand.routeProtocolType
            val routeProtocolFiles = filterCsvFilesFromFilesAndDirs(Parsing.ResultCommand.routeProtocols)
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

            val participantsListFile = try {
                getExistingReadableFile(Parsing.ResultTeamsCommand.participants)
            } catch (e: Exception) {
                Logger.error {"Couldn't find participants list file. A following exception."}
                Logger.error(e)
                return
            }
            val resultProtocolFiles = filterCsvFilesFromFilesAndDirs(Parsing.ResultTeamsCommand.resultProtocols)
            if (resultProtocolFiles.isEmpty()) {
                Logger.error {"No result protocols were specified. Exiting."}
                return
            }

            Logger.debug {"Participants list file: $participantsListFile"}
            Logger.debug {"Route protocol files: $resultProtocolFiles"}

            TODO("Режим 3: сгенерировать протокол командных результатов по имеющимся результатам")
        }
    }
}

