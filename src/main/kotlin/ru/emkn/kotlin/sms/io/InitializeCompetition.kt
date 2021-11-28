package ru.emkn.kotlin.sms.io

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.GroupRequirement
import ru.emkn.kotlin.sms.Route
import java.io.File

fun checkAndReadFileInFolder(folderPath : String, fileName : String) : List<String> {
    if (!File("$folderPath/$fileName").exists())
        throw IllegalArgumentException("!File '$fileName' is missed!")
    return File("$folderPath/$fileName").readLines()
}

fun initializeCompetition(configFolderPath : String) : Competition {
    Logger.debug { "Start initializing competition" }
    if (!File(configFolderPath).exists() || !File(configFolderPath).isDirectory)
        throw IllegalArgumentException("Config path is not correct!")

    val nameAndDate = checkAndReadFileInFolder(configFolderPath, "Name_and_date.csv")
    if (nameAndDate.size < 4)
        throw IllegalArgumentException("'Name_and_date' is not correct! Please, check Readme and fix.")
    val discipline = nameAndDate[0]
    val name = nameAndDate[1]
    requireNotNull(nameAndDate[2].toIntOrNull()) { "In third line of 'Name_and_date' must be a number." }
    val year = nameAndDate[2].toInt()
    val date = nameAndDate[3]
    Logger.info { "Initialized name and date" }

    val routeOfGroups = checkAndReadFileInFolder(configFolderPath, "Route_of_groups")
    val groups = routeOfGroups.mapIndexed { ind, row ->
        if (row.count {it == ','} != 1)
            throw IllegalArgumentException("Wrong number of commas in file 'Route_of_groups' in line $ind! " +
                    "Should be only one.")
        row.split(',')[0]
    }
    Logger.info { "Initialized group routes" }

    val routeDescription = checkAndReadFileInFolder(configFolderPath, "Route_description")
    routeDescription.forEach { row ->
        if (row.count { c -> c == ',' } == 0)
            throw IllegalArgumentException("Line $row of 'Route_description' has no commas!")
    }
    val routes = routeDescription.mapIndexed { ind, row ->
        val splittedRow = row.split(',')
        Route(splittedRow[0], splittedRow.subList(1, splittedRow.size))
    }
    Logger.info { "Initialized routes checkpoints" }

    val groupToRouteMapping = routeOfGroups.associate { row ->
        val splittedRow = row.split(',')
        val route = routes.find { it.name == splittedRow[1] }
        if (route == null)
            throw IllegalArgumentException("Routes in 'Route_description' and in 'Route_of_groups' don't match!")
        Pair(splittedRow[0], route)
    }
    Logger.info { "Mapped group to route" }

    val groupRequirement = checkAndReadFileInFolder(configFolderPath, "Groups_requirement")
    val requirementByGroup = groupRequirement.associate { row ->
        val splittedRow = row.split(',')
        if (splittedRow.size != 3)
            throw IllegalArgumentException("Number of commas in $row line in 'Groups_requirement' incorrect! " +
                    "Should be exactly three.")
        requireNotNull(splittedRow[1].toIntOrNull()) { "First parameter in $row line should be integer! " +
                "Was '${splittedRow[1]}'." }
        requireNotNull(splittedRow[2].toIntOrNull()) { "Second parameter in $row line should be integer! " +
                "Was '${splittedRow[2]}'." }
        Pair(splittedRow[0], GroupRequirement(splittedRow[1].toInt(), splittedRow[2].toInt()))
    }
    for (g in groups)
        if (!requirementByGroup.containsKey(g))
            throw IllegalArgumentException("Requirements for group $g is missed!")
    return Competition(discipline, name, year, date, groups, routes, groupToRouteMapping, requirementByGroup)
}