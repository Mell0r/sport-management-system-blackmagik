package ru.emkn.kotlin.sms.gui.builders

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.github.michaelbull.result.*
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.io.initializeCompetition

/**
 * aka "Mutable competition": a class which allows to
 * configure a desired competition,
 * and then build an instance of [Competition] class.
 */
class CompetitionBuilder(
    val discipline: MutableState<String> = mutableStateOf(""),
    val name: MutableState<String> = mutableStateOf(""),
    val year: MutableState<Int> = mutableStateOf(INCORRECT_YEAR),
    val date: MutableState<String> = mutableStateOf(""),
    val groups: SnapshotStateList<AgeGroupBuilder> = mutableStateListOf(),
    val routes: SnapshotStateList<OrderedCheckpointsRouteBuilder> = mutableStateListOf(),
) {

    companion object {
        const val INCORRECT_YEAR = -6666
    }

    /**
     * Replaces all the data in the builder with data from [competition].
     * Useful for loading competition and then modifying it in GUI.
     */
    fun replaceFromCompetition(competition: Competition) {
        discipline.value = competition.discipline
        name.value = competition.name
        year.value = competition.year
        date.value = competition.date
        groups.clear()
        groups.addAll(
            competition.groups.filterIsInstance<AgeGroup>()
                .map {
                    AgeGroupBuilder(
                        mutableStateOf(it.label),
                        mutableStateOf(it.route.name),
                        mutableStateOf(it.ageFrom.toString()),
                        mutableStateOf(it.ageTo.toString())
                    )
                }.toMutableStateList(),
        )
        routes.clear()
        routes.addAll(
            competition.routes.filterIsInstance<OrderedCheckpointsRoute>()
                .map { route ->
                    OrderedCheckpointsRouteBuilder(
                        mutableStateOf(route.name),
                        route.orderedCheckpoints.map { mutableStateOf(it) }
                            .toMutableStateList()
                    )
                }.toMutableStateList(),
        )
    }

    /**
     * Replaces all the data in the builder with data
     * from files in directory [configFolderPath]
     * in format consistent with [initializeCompetition].
     *
     * @throws [IllegalArgumentException] if something went wrong.
     */
    fun replaceFromFilesInFolder(configFolderPath: String): UnitOrMessage {
        return initializeCompetition(configFolderPath).map { competition ->
            replaceFromCompetition(competition)
        }
    }

    /**
     * Returns built instance of [Competition] class.
     */
    fun build(): Competition {
        val routes = routes.map { it.toOrderedCheckpointsRoute() }.toList()
        val groups = groups.map { ageGroupBuilder ->
            val route = routes.single { it.name == ageGroupBuilder.routeName.value }
            AgeGroup(
                label = ageGroupBuilder.label.value,
                route = route,
                ageFrom = ageGroupBuilder.ageFrom.value.toInt(),
                ageTo = ageGroupBuilder.ageTo.value.toInt(),
                competitionYear = year.value,
            )
        }
        return Competition(
            discipline = discipline.value,
            name = name.value,
            year = year.value,
            date = date.value,
            groups = groups,
            routes = routes,
        )
    }
}