package ru.emkn.kotlin.sms.gui

import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.Group
import ru.emkn.kotlin.sms.Route

/**
 * aka "Mutable competition": a class which allows to
 * configure a desired competition, and then build an
 * instance of Competition class.
 */
class CompetitionBuilder (
    var discipline: String = "",
    var name: String = "",
    var year: Int = 0,
    var date: String = "",
) {
    val groupsBuilder = UniqueListBuilder<Group>(
        equals = { group1, group2 -> group1.label == group2.label }
    )
    val routesBuilder = UniqueListBuilder<Route>(
        equals = { route1, route2 -> route1.name == route2.name }
    )

    companion object {
        /**
         * Creates a [CompetitionBuilder] from [Competition].
         * Useful for loading competition and then modifying it in GUI.
         */
        fun fromCompetition(competition: Competition): CompetitionBuilder {
            val builder = CompetitionBuilder(
                discipline = competition.discipline,
                name = competition.name,
                year = competition.year,
                date = competition.date,
            )
            builder.groupsBuilder.replaceList(competition.groups)
            builder.routesBuilder.replaceList(competition.routes)
            return builder
        }
    }

    /**
     * Returns built instance of [Competition] class.
     */
    fun build(): Competition {
        TODO()
    }
}