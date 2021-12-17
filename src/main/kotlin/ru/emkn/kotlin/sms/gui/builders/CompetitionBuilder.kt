package ru.emkn.kotlin.sms.gui.builders

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.Group
import ru.emkn.kotlin.sms.OrderedCheckpointsRoute
import ru.emkn.kotlin.sms.io.initializeCompetition

/**
 * aka "Mutable competition": a class which allows to
 * configure a desired competition,
 * and then build an instance of [Competition] class.
 */
class CompetitionBuilder(
    val discipline: MutableState<String> = mutableStateOf(""),
    val name: MutableState<String> = mutableStateOf(""),
    val year: MutableState<Int> = mutableStateOf(0),
    val date: MutableState<String> = mutableStateOf(""),
    val groups: SnapshotStateList<Group> = mutableStateListOf(),
    val routes: SnapshotStateList<OrderedCheckpointsRouteBuilder> = mutableStateListOf(),
) {
    companion object {
        /**
         * Creates a new [CompetitionBuilder] with data from Competition.
         * Useful for loading competition and then modifying it in GUI.
         */
        fun fromCompetition(competition: Competition) = CompetitionBuilder(
            discipline = mutableStateOf(competition.discipline),
            name = mutableStateOf(competition.name),
            year = mutableStateOf(competition.year),
            date = mutableStateOf(competition.date),
            groups = competition.groups.toMutableStateList(),
            routes = competition.routes.filterIsInstance<OrderedCheckpointsRoute>()
                .map {
                    OrderedCheckpointsRouteBuilder(
                        it.name,
                        it.orderedCheckpoints.map { mutableStateOf(it) }
                            .toMutableStateList()
                    )
                }.toMutableStateList(),
        )

        /**
         * Creates a new [CompetitionBuilder] with data
         * from files in directory [configFolderPath]
         * in format consistent with [initializeCompetition].
         *
         * @throws [IllegalArgumentException] if something went wrong.
         */
        fun fromFilesInFolder(configFolderPath: String): CompetitionBuilder {
            val competition = initializeCompetition(configFolderPath)
            return fromCompetition(competition)
        }
    }

    /**
     * Returns built instance of [Competition] class.
     */
    fun build(): Competition {
        return Competition(
            discipline = discipline.value,
            name = name.value,
            year = year.value,
            date = date.value,
            groups = groups.toMutableList().toList(),
            routes = routes.map {
                OrderedCheckpointsRoute(
                    it.name,
                    it.orderedCheckpoints.map { it.value }.toMutableList()
                )
            }.toMutableList().toList(),
        )
    }
}