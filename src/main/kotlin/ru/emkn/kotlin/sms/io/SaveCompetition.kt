package ru.emkn.kotlin.sms.io

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.Competition
import java.io.File

/**
 * Saves [competition] into files in [configFolderPath] folder
 * in format consistent with [initializeCompetition] in InitializeCompetition.kt.
 * Overwrites files if they already exist.
 *
 * @throws [IllegalArgumentException] if an IO exception happened.
 */
fun saveCompetition(competition: Competition, configFolderPath: String) {
    Logger.debug { "Beginning to save competition to directory \"$configFolderPath\"." }
    val directory = File(configFolderPath)
    require(!directory.isDirectory) {
        "Config folder \"$configFolderPath\" is not a directory!"
    }
    require(directory.mkdirs()) {
        "Could not create all necessary directories for directory \"$configFolderPath\"."
    }
    saveNameAndDate(competition, directory)
    saveRouteOfGroups(competition, directory)
    saveRouteDescription(competition, directory)
    saveGroupsRequirement(competition, directory)
    Logger.debug { "Successfully finished saving competition to directory \"$configFolderPath\"." }
}

private fun saveNameAndDate(competition: Competition, directory: File) {
    Logger.debug { "Beginning to save to $NAME_AND_DATE_FILENAME." }
    val file = File("${directory.absolutePath}/$NAME_AND_DATE_FILENAME")
    file.createNewFile()
    val builder = StringBuilder()

    builder.append("${competition.discipline}\n")
    builder.append("${competition.name}\n")
    builder.append("${competition.year}\n")
    builder.append("${competition.date}\n")

    file.writeText(builder.toString())
    Logger.debug { "Finished saving to $NAME_AND_DATE_FILENAME." }
}

private fun saveRouteOfGroups(competition: Competition, directory: File) {
    Logger.debug { "Beginning to save to $ROUTE_OF_GROUPS_FILENAME." }
    val file = File("${directory.absolutePath}/$ROUTE_OF_GROUPS_FILENAME")
    file.createNewFile()
    val builder = StringBuilder()

    competition.groups.forEach { group ->
        builder.append("${group.label},${group.route.name}\n")
    }

    file.writeText(builder.toString())
    Logger.debug { "Finished saving to $ROUTE_OF_GROUPS_FILENAME." }
}

private fun saveRouteDescription(competition: Competition, directory: File) {
    Logger.debug { "Beginning to save to $ROUTE_DESCRIPTION_FILENAME." }
    val file = File("${directory.absolutePath}/$ROUTE_DESCRIPTION_FILENAME")
    file.createNewFile()
    val builder = StringBuilder()

    competition.routes.forEach { route ->
        builder.append("${route.dumpToCsvString()}\n")
    }

    file.writeText(builder.toString())
    Logger.debug { "Finished saving to $ROUTE_DESCRIPTION_FILENAME." }
}

private fun saveGroupsRequirement(competition: Competition, directory: File) {
    Logger.debug { "Beginning to save to $GROUPS_REQUIREMENT_FILENAME." }
    val file = File("${directory.absolutePath}/$GROUPS_REQUIREMENT_FILENAME")
    file.createNewFile()
    val builder = StringBuilder()

    competition.groups.forEach { group ->
        builder.append("${group.dumpToCsvString()}\n")
    }

    file.writeText(builder.toString())
    Logger.debug { "Finished saving to $GROUPS_REQUIREMENT_FILENAME." }
}