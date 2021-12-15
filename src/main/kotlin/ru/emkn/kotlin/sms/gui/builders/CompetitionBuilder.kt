package ru.emkn.kotlin.sms.gui.builders

import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.Group
import ru.emkn.kotlin.sms.Route
import ru.emkn.kotlin.sms.io.initializeCompetition

/**
 * aka "Mutable competition": a class which allows to
 * configure a desired competition,
 * and then build an instance of [Competition] class.
 */
class CompetitionBuilder {
    var discipline: String = ""
        set(value) { field = value; notifyAllListeners() }
    var name: String = ""
        set(value) { field = value; notifyAllListeners() }
    var year: Int = 0
        set(value) { field = value; notifyAllListeners() }
    var date: String = ""
        set(value) { field = value; notifyAllListeners() }

    private val groupsBuilder = UniqueListBuilder<Group>(
        equals = { group1, group2 -> group1.label == group2.label }
    )
    private val routesBuilder = UniqueListBuilder<Route>(
        equals = { route1, route2 -> route1.name == route2.name }
    )

    private val listeners: MutableList<BuilderListener<CompetitionBuilder>> = mutableListOf()
    fun addListener(listener: BuilderListener<CompetitionBuilder>) {
        listeners.add(listener)
    }

    private fun notifyAllListeners() {
        listeners.forEach {
            it.dataChanged(this)
        }
    }

    /**
     * Replaces all data in builder with data from [Competition].
     * Useful for loading competition and then modifying it in GUI.
     *
     * @throws [IllegalArgumentException] if something went wrong.
     */
    fun replaceFromCompetition(competition: Competition) {
        discipline = competition.discipline
        name = competition.name
        year = competition.year
        date = competition.date
        require(groupsBuilder.replaceList(competition.groups))
        require(routesBuilder.replaceList(competition.routes))
        notifyAllListeners()
    }

    /**
     * Replaces all data in builder with data
     * from files in directory [configFolderPath]
     * in format consistent with [initializeCompetition].
     *
     * @return true if the replacement was successful, false if the config files were corrupted
     */
    fun replaceFromFilesInFolder(configFolderPath: String) : Boolean {
        val competition = try {
            initializeCompetition(configFolderPath)
        } catch (e: IllegalArgumentException) {
            return false
        }
        replaceFromCompetition(competition)
        return true
    }

    /**
     * Returns built instance of [Competition] class.
     */
    fun build(): Competition {
        TODO()
    }

    private fun checkModification(modified: Boolean) : Boolean {
        if (modified) {
            notifyAllListeners()
        }
        return modified
    }

    /**
     * @return true if it could successfully add [group]
     * false if a group with the same name already exists.
     */
    fun addGroup(group: Group) : Boolean {
        TODO()
    }

    /**
     * @return true if it could successfully remove a [group]
     * false if the given group was not present in the list.
     */
    fun removeGroup(group: Group) : Boolean {
        TODO()
    }

    /**
     * @return true if it could successfully add [route]
     * false if a route with the same name already exists.
     */
    fun addRoute(route: Route) : Boolean {
        TODO()
    }

    /**
     * @return true if it could successfully remove a [route]
     * false if the given route was not present in the list.
     */
    fun removeRoute(route: Route) : Boolean {
        TODO()
    }

}