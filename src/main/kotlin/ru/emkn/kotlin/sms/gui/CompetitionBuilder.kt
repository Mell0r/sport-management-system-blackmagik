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
    var discipline: String,
    var name: String,
    var year: Int,
    var date: String,
    private val groups: MutableList<Group>, // all groups are guaranteed to be unique
    private val routes: MutableList<Route>, // all routes are guaranteed to be unique
) {
    /**
     * Returns built instance of [Competition] class.
     */
    fun build(): Competition {
        TODO()
    }

    val competition: Competition
        get() = build()

    /**
     * Tries to add a new group.
     *
     * @param [group] to be added.
     *
     * @throws [IllegalArgumentException] if
     * a group with the same name already exists
     * or some other error occurs.
     */
    fun addGroup(group: Group) {
        TODO()
    }

    /**
     * Tries to remove a group.
     * Does nothing if the [group] is not in [groups] list.
     *
     * @param [group] to be removed.
     */
    fun removeGroup(group: Group) {
        TODO()
    }

    /**
     * Tries to add a new route.
     *
     * @param [route] to be added.
     *
     * @throws [IllegalArgumentException] if
     * a route with the same name already exists
     * or some other error occurs.
     */
    fun addRoute(route: Route) {
        TODO()
    }

    /**
     * Tries to remove a route.
     * Does nothing if the [route] is not in [routes] list.
     *
     * @param [route] to be removed.
     */
    fun removeRoute(route: Route) {
        TODO()
    }
}