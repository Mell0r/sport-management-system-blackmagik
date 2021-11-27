package ru.emkn.kotlin.sms

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.time.Time
import java.io.File

fun checkAndReadFileInFolder(folderPath : String, fileName : String) : List<String> {
    if (!File("$folderPath/$fileName").exists())
        throw IllegalArgumentException("!File '$fileName' is missed!")
    return File("$folderPath/$fileName").readLines()
}

class GroupRequirement(private val ageFrom: Int, private val ageTo: Int) {
    fun checkApplicant(age : Int) = (age >= ageFrom) && (age <= ageTo)
}

class Competition {
    val discipline: String
    val name: String
    val year: Int
    val date: String
    val groups: List<String>
    val routes: List<Route>
    val groupToRouteMapping: Map<String, Route>
    val requirementByGroup: Map<String, GroupRequirement>
    constructor(
        discipline: String, name: String, year: Int, date: String,
        groups: List<String>, routes : List<Route>,
        groupToRouteMapping: Map<String, Route>,
        requirementByGroup : Map<String, GroupRequirement>) {
        this.discipline = discipline
        this.name = name
        this.year = year
        this.date = date
        this.groups = groups
        this.routes = routes
        this.groupToRouteMapping = groupToRouteMapping
        this.requirementByGroup = requirementByGroup
    }

    constructor(configFolderPath : String) {
        Logger.info { "Start initializing competition" }
        if (!File(configFolderPath).exists() || !File(configFolderPath).isDirectory)
            throw IllegalArgumentException("Config path is not correct!")

        val nameAndDate = checkAndReadFileInFolder(configFolderPath, "Name_and_date")
        if (nameAndDate.size < 4)
            throw IllegalArgumentException("'Name_and_date' is not correct! Please, check Readme and fix.")
        discipline = nameAndDate[0]
        name = nameAndDate[1]
        requireNotNull(nameAndDate[2].toIntOrNull()) { "In third line of 'Name_and_date' must be a number." }
        year = nameAndDate[2].toInt()
        date = nameAndDate[3]
        Logger.info { "Initialized name and date" }

        val routeOfGroups = checkAndReadFileInFolder(configFolderPath, "Route_of_groups")
        groups = routeOfGroups.mapIndexed { ind, row ->
            if (row.count {it == ','} != 1)
                throw IllegalArgumentException("Wrong number of commas in file 'Route_of_groups' in line $ind! " +
                        "Should be only one.")
            row.split(',')[0]
        }
        Logger.info { "Initialized group routes" }

        val routeDescription = checkAndReadFileInFolder(configFolderPath, "Route_description")
        if (routeDescription.any { it.count { c -> c == ',' } != routeDescription[0].count {c -> c == ','} })
            throw IllegalArgumentException("Numbers of commas in different lines don't match in file 'Route_description'!")
        routes = routeDescription.mapIndexed { ind, row ->
            val splittedRow = row.split(',')
            Route(splittedRow[0], splittedRow.subList(1, splittedRow.size))
        }
        Logger.info { "Initialized routes checkpoints" }

        groupToRouteMapping = routeOfGroups.associate { row ->
            val splittedRow = row.split(',')
            val route = routes.find { it.name == splittedRow[1] }
            if (route == null)
                throw IllegalArgumentException("Routes in 'Route_description' and in 'Route_of_groups' don't match!")
            Pair(splittedRow[0], route)
        }
        Logger.info { "Mapped group to route" }

        val groupRequirement = checkAndReadFileInFolder(configFolderPath, "Groups_requirement")
        requirementByGroup = groupRequirement.associate { row ->
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
    }
}