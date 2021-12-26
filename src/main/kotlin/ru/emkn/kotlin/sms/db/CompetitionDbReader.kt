package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.sql.Database
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.db.schema.GroupEntity
import ru.emkn.kotlin.sms.db.schema.GroupsTable
import ru.emkn.kotlin.sms.db.schema.RoutesTable
import ru.emkn.kotlin.sms.db.util.DbReader


/**
 * Reads [Competition] from multiple tables in [database].
 */
class CompetitionDbReader(
    private val database: Database
) {
    /**
     * Reads groups from [GroupsTable] in [database]
     * based on knowledge of [routes] and [competitionYear].
     */
    fun readGroups(routes: List<Route>, competitionYear: Int): ResultOrMessage<List<Group>> {
        val parser = GroupEntityParser(competitionYear, routes)
        val reader = DbReader(
            database = database,
            table = GroupsTable,
            entityClass = GroupEntity,
            entityParser = parser,
        )
        return reader.read()
    }

    /**
     * Reads routes from [RoutesTable] in [database].
     */
    fun readRoutes(): ResultOrMessage<List<Route>> {
        TODO()
    }
}