package ru.emkn.kotlin.sms.io

import com.github.michaelbull.result.*
import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.*
import java.io.File
import java.io.IOException

const val NAME_AND_DATE_FILENAME = "Name_and_date.csv"
const val ROUTE_OF_GROUPS_FILENAME = "Route_of_groups.csv"
const val ROUTE_DESCRIPTION_FILENAME = "Route_description.csv"
const val GROUPS_REQUIREMENT_FILENAME = "Groups_requirement.csv"

fun checkAndReadFileInFolder(
    folderPath: String, fileName: String
): ResultOrMessage<FileContent> {
    val file = File("$folderPath/$fileName")
    if (!file.exists()) return Err("!File '$fileName' is missed!")
    return runCatching { file.readLines() }.mapError {
        when (it) {
            is IOException -> it.message
            else -> throw it
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
fun initializeCompetition(configFolderPath: String): ResultOrMessage<Competition> {
    return binding {
        Logger.debug { "Start initializing competition" }
        if (!(File(configFolderPath).exists() && File(configFolderPath).isDirectory)) Err(
            "Config path is not correct!"
        ).bind<Nothing>()

        // NAME_AND_DATE

        val nameAndDate = readNameAndDate(configFolderPath).bind()
        val discipline = nameAndDate[0]
        val name = readName(nameAndDate).bind()
        val year = readYear(nameAndDate).bind()
        val date = nameAndDate[3]
        Logger.info { "Initialized $NAME_AND_DATE_FILENAME" }

        // ROUTE_OF_GROUPS
        val routesOfGroupsFileContent = checkAndReadFileInFolder(
            configFolderPath, ROUTE_OF_GROUPS_FILENAME
        ).bind()
        val groups = readGroupsLabels(routesOfGroupsFileContent).bind()
        Logger.info { "Initialized $ROUTE_OF_GROUPS_FILENAME" }

        // ROUTE_DESCRIPTION
        val routes = readRoutes(configFolderPath).bind()
        Logger.info { "Initialized $ROUTE_DESCRIPTION_FILENAME" }


        val groupToRouteMapping =
            readGroupToRouteMapping(routesOfGroupsFileContent, routes).bind()
        Logger.info { "Mapped groups to routes" }

        // GROUPS_REQUIREMENT
        val groupRequirementFileContent = checkAndReadFileInFolder(
            configFolderPath, GROUPS_REQUIREMENT_FILENAME
        ).bind()
        val requirementByGroup = readRequirementsByGroups(
            groupRequirementFileContent, groupToRouteMapping, year, groups
        ).bind()
        Competition(
            discipline,
            name,
            year,
            date,
            requirementByGroup.values.toList(),
            routes,
        )
    }
}

private fun readGroupToRouteMapping(
    routesOfGroupsFileContent: FileContent, routes: List<Route>
) = routesOfGroupsFileContent.mapResult { row ->
    val routeMismatchMessage =
        "Routes in $ROUTE_DESCRIPTION_FILENAME and in $ROUTE_OF_GROUPS_FILENAME don't match!"
    val tokens = row.split(',')
    val route = routes.find { it.name == tokens[1] } ?: return@mapResult Err(
        routeMismatchMessage
    )
    Ok(tokens[0] to route)
}.map(List<Pair<String, Route>>::toMap)

@OptIn(ExperimentalStdlibApi::class)
private fun readRoutes(configFolderPath: String): ResultOrMessage<List<Route>> {
    return checkAndReadFileInFolder(
        configFolderPath, ROUTE_DESCRIPTION_FILENAME
    ).andThen { routeDescription ->
        routeDescription.forEachIndexed { ind, row ->
            if (row.count { c -> c == ',' } == 0) return Err("Line $ind of $ROUTE_DESCRIPTION_FILENAME has no commas!")
        }
        val routes = routeDescription.mapResultIndexed { ind, row ->
            readRouteFromLine(row).mapError { eMessage ->
                ("Line $ind: ${eMessage}.")
            }
        }
        routes
    }
}

private fun readGroupsLabels(routesOfGroupsFileContent: FileContent): ResultOrMessage<List<String>> {
    val groups = routesOfGroupsFileContent.mapResultIndexed { ind, row ->
        if (row.count { it == ',' } != 1) {
            Err(
                "Wrong number of commas in file $ROUTE_OF_GROUPS_FILENAME "
                        + "in line $ind! Should be only one."
            )
        } else Ok(row.split(',')[0])
    }
    return groups
}

private fun readYear(nameAndDate: FileContent): ResultOrMessage<Int> {
    val year = nameAndDate[2].toIntOrNull()
        ?: return Err("In third line of $NAME_AND_DATE_FILENAME must be a number.")
    return Ok(year)
}

private fun readName(nameAndDate: FileContent): ResultOrMessage<String> {
    val name = nameAndDate[1]
    return Ok(name)
}

private fun readNameAndDate(configFolderPath: String): ResultOrMessage<FileContent> {
    val nameDateFileIncorrect =
        "$NAME_AND_DATE_FILENAME is not correct! Please, check Readme and fix."
    return checkAndReadFileInFolder(
        configFolderPath, NAME_AND_DATE_FILENAME
    ).andThen { nameAndDate ->
        if (nameAndDate.size < 4) Err(nameDateFileIncorrect)
        else Ok(nameAndDate)
    }
}

private fun readRequirementsByGroups(
    groupRequirement: FileContent,
    groupToRouteMapping: Map<String, Route>,
    year: Int,
    groups: List<String>
): ResultOrMessage<Map<String, AgeGroup>> {
    val requirementByGroupOrError = groupRequirement.mapResult { row ->
        val tokens = row.split(',')
        if (tokens.size != 3) return@mapResult errAndLog(
            "Number of commas in $row line in $GROUPS_REQUIREMENT_FILENAME " +
                    "incorrect! Should be exactly three."
        )

        val label = tokens[0]
        val ageFrom = tokens[1].toIntOrNull() ?: return errAndLog(
            "First parameter in $row line should be integer! " + "Was '${tokens[1]}'."
        )
        val ageTo = tokens[2].toIntOrNull() ?: return Err(
            "Second parameter in $row line should be integer! " + "Was '${tokens[2]}'."
        )
        val route = groupToRouteMapping[label] ?: return Err(
            "No route specified for group $label."
        )
        Ok(
            label to AgeGroup(
                label = label,
                route = route,
                ageFrom = ageFrom,
                ageTo = ageTo,
                competitionYear = year,
            )
        )
    }.map(List<Pair<String, AgeGroup>>::toMap)
    return requirementByGroupOrError.andThen { requirementByGroup ->
        for (g in groups) {
            if (!requirementByGroup.containsKey(g))
                return Err("Requirements for group $g is missed!")
        }
        Ok(requirementByGroup)
    }
}

