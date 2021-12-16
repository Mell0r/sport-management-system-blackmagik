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


    var groups: MutableList<Group> = mutableListOf()
    var routes: MutableList<Route> = mutableListOf()

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
        groups = competition.groups.toMutableList()
        routes = competition.routes.toMutableList()
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
}