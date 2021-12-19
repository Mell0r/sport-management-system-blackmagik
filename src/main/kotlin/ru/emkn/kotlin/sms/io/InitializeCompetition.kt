package ru.emkn.kotlin.sms.io

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.AgeGroup
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.readRouteFromLine
import ru.emkn.kotlin.sms.results_processing.FileContent
import java.io.File

const val NAME_AND_DATE_FILENAME = "Name_and_date.csv"
const val ROUTE_OF_GROUPS_FILENAME = "Route_of_groups.csv"
const val ROUTE_DESCRIPTION_FILENAME = "Route_description.csv"
const val GROUPS_REQUIREMENT_FILENAME = "Groups_requirement.csv"

fun checkAndReadFileInFolder(
    folderPath: String,
    fileName: String
): FileContent {
    val file = File("$folderPath/$fileName")
    if (!file.exists())
        throw IllegalArgumentException("!File '$fileName' is missed!")
    return file.readLines()
}

@OptIn(ExperimentalStdlibApi::class)
fun initializeCompetition(configFolderPath: String): Competition {
    Logger.debug { "Start initializing competition" }
    if (!File(configFolderPath).exists() || !File(configFolderPath).isDirectory)
        throw IllegalArgumentException("Config path is not correct!")

    // NAME_AND_DATE
    val nameAndDate =
        checkAndReadFileInFolder(configFolderPath, NAME_AND_DATE_FILENAME)
    if (nameAndDate.size < 4)
        throw IllegalArgumentException("$NAME_AND_DATE_FILENAME is not correct! Please, check Readme and fix.")
    val discipline = nameAndDate[0]
    val name = nameAndDate[1]
    requireNotNull(nameAndDate[2].toIntOrNull()) { "In third line of $NAME_AND_DATE_FILENAME must be a number." }
    val year = nameAndDate[2].toInt()
    val date = nameAndDate[3]
    Logger.info { "Initialized $NAME_AND_DATE_FILENAME" }

    // ROUTE_OF_GROUPS
    val routeOfGroups =
        checkAndReadFileInFolder(configFolderPath, ROUTE_OF_GROUPS_FILENAME)
    val groups = routeOfGroups.mapIndexed { ind, row ->
        if (row.count { it == ',' } != 1)
            throw IllegalArgumentException(
                "Wrong number of commas in file $ROUTE_OF_GROUPS_FILENAME in line $ind! " +
                        "Should be only one."
            )
        row.split(',')[0]
    }
    Logger.info { "Initialized $ROUTE_OF_GROUPS_FILENAME" }

    // ROUTE_DESCRIPTION
    val routeDescription =
        checkAndReadFileInFolder(configFolderPath, ROUTE_DESCRIPTION_FILENAME)
    routeDescription.forEachIndexed { ind, row ->
        if (row.count { c -> c == ',' } == 0)
            throw IllegalArgumentException("Line $ind of $ROUTE_DESCRIPTION_FILENAME has no commas!")
    }
    val routes = routeDescription.mapIndexed { ind, row ->
        try {
            readRouteFromLine(row)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Line $ind: ${e.message}.")
        }
    }
    Logger.info { "Initialized $ROUTE_DESCRIPTION_FILENAME" }

    val groupToRouteMapping = routeOfGroups.associate { row ->
        val tokens = row.split(',')
        val route = routes.find { it.name == tokens[1] }
            ?: throw IllegalArgumentException("Routes in $ROUTE_DESCRIPTION_FILENAME and in $ROUTE_OF_GROUPS_FILENAME don't match!")
        Pair(tokens[0], route)
    }
    Logger.info { "Mapped group to route" }

    // GROUPS_REQUIREMENT
    val groupRequirement =
        checkAndReadFileInFolder(configFolderPath, GROUPS_REQUIREMENT_FILENAME)
    val requirementByGroup = groupRequirement.associate { row ->
        val tokens = row.split(',')
        if (tokens.size != 3)
            throw IllegalArgumentException(
                "Number of commas in $row line in $GROUPS_REQUIREMENT_FILENAME incorrect! " +
                        "Should be exactly three."
            )
        val label = tokens[0]
        val ageFrom = tokens[1].toIntOrNull()
        requireNotNull(ageFrom) {
            "First parameter in $row line should be integer! " +
                    "Was '${tokens[1]}'."
        }
        val ageTo = tokens[2].toIntOrNull()
        requireNotNull(ageTo) {
            "Second parameter in $row line should be integer! " +
                    "Was '${tokens[2]}'."
        }
        val route = groupToRouteMapping[label]
        requireNotNull(route) {
            "No route specified for group $label."
        }
        Pair(
            label,
            AgeGroup(
                label = label,
                route = route,
                ageFrom = ageFrom,
                ageTo = ageTo,
            ),
        )
    }
    for (g in groups)
        if (!requirementByGroup.containsKey(g))
            throw IllegalArgumentException("Requirements for group $g is missed!")
    return Competition(
        discipline,
        name,
        year,
        date,
        requirementByGroup.values.toList(),
        routes,
    )
}