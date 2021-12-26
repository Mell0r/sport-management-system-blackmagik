package ru.emkn.kotlin.sms.csv

import com.github.michaelbull.result.*
import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.UnitOrMessage
import java.io.File

/**
 * Saves [competition] into files in [configFolderPath] folder
 * in format consistent with [initializeCompetition] in InitializeCompetition.kt.
 * It overwrites files if they already exist.
 *
 * @throws [IllegalArgumentException] if an IO exception happened.
 */
fun saveCompetition(competition: Competition, configFolderPath: String): UnitOrMessage {
    Logger.debug { "Beginning to save competition to directory \"$configFolderPath\"." }
    val result = runCatching {
        val directory = File(configFolderPath)
        if (directory.exists()) {
            require(directory.isDirectory) {
                "Config folder exists and is not \"$configFolderPath\" is not a directory!"
            }
        } else {
            directory.mkdirs()
        }
        saveNameAndDate(competition, directory)
        saveRouteOfGroups(competition, directory)
        saveRouteDescription(competition, directory)
        saveGroupsRequirement(competition, directory)
    }
    return result.mapEither(
        success = {
            Logger.debug("Successfully finished saving competition to directory \"$configFolderPath\".")
        },
        failure = {
            Logger.debug {
                "Some error happened while saving competition to to directory \"$configFolderPath\".\n" +
                        it.message
            }
            it.message
        },
    )
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