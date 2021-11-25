package ru.emkn.kotlin.sms

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.time.Time
import java.io.File

fun checkAndReadFileInFolder(folderPath : String, fileName : String) : List<String> {
    if (!File("$folderPath/$fileName").exists())
        throw IllegalArgumentException("File '$fileName' is missed!")
    return File("$folderPath/$fileName").readLines()
}

class Competition(configFolderPath : String) {
    val discipline: String
    val name: String
    val date: Time
    val groups: List<String>
    val routes: List<Route>
    val groupToRouteMapping: Map<String, Route>
    init {
        Logger.info {"Start initializing competition" }
        if (!File(configFolderPath).exists() || !File(configFolderPath).isDirectory)
            throw IllegalArgumentException("Config path is not correct!")

        val nameAndDate = checkAndReadFileInFolder(configFolderPath, "Name_and_date")
        if (nameAndDate.size < 3)
            throw IllegalArgumentException("'Name_and_date' is not correct! Please, check Readme and fix.")
        discipline = nameAndDate[0]
        name = nameAndDate[1]
        date = Time.fromString(nameAndDate[2])

        val routeOfGroups = checkAndReadFileInFolder(configFolderPath, "Route_of_groups")
        groups = routeOfGroups.mapIndexed { ind, row ->
            if (row.count {it == ','} != 1)
                throw IllegalArgumentException("Wrong number of commas in file 'Route_of_groups' in row $ind! " +
                        "Should be only one.")
            row.split(',')[0]
        }

        val routeDescription = checkAndReadFileInFolder(configFolderPath, "Route_description")
        if (routeDescription.any { it.count { c -> c == ',' } != routeDescription[0].count {c -> c == ','} })
            throw IllegalArgumentException("Numbers of commas in different lines don't match in file 'Route_description'!")
        routes = routeDescription.mapIndexed { ind, row ->
            val splittedRow = row.split(',')
            splittedRow.subList(1, splittedRow.size).forEach {
                    require(it.toIntOrNull() != null) { "One of the route marks are not a number in $row row" }
                }
            Route(splittedRow[0], splittedRow.subList(1, splittedRow.size).map { it.toInt() })
        }

        groupToRouteMapping = routeOfGroups.associate {
            Pair(it.split(',')[0], getRouteByName(it.split(',')[1]))
        }
    }

    fun getRouteByName(name: String): Route {
        return routes.find { it.name == name } ?: throw IllegalArgumentException("Route with the given name does not exists")
    }
}