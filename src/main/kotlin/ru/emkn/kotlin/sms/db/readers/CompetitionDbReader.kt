package ru.emkn.kotlin.sms.db.readers

import org.jetbrains.exposed.sql.Database
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.db.util.loggingTransaction
import com.github.michaelbull.result.*
import org.jetbrains.exposed.sql.selectAll
import ru.emkn.kotlin.sms.db.CompetitionHeader
import ru.emkn.kotlin.sms.db.parsers.CompetitionHeaderResultRowParser
import ru.emkn.kotlin.sms.db.parsers.GroupEntityParser
import ru.emkn.kotlin.sms.db.parsers.RouteEntityParser
import ru.emkn.kotlin.sms.db.schema.*


/**
 * Reads [Competition] from multiple tables in [database].
 */
class CompetitionDbReader(
    private val database: Database
) {
    /**
     * Reads [CompetitionHeader] from [CompetitionHeaderTable] in [database].
     */
    fun readHeader(): ResultOrMessage<CompetitionHeader> {
        val reader = DbResultRowReader(
            database = database,
            table = CompetitionHeaderTable,
            resultRowParser = CompetitionHeaderResultRowParser,
        )
        return reader.readFirst()
    }

    /**
     * Reads routes from [RoutesTable] in [database].
     */
    fun readRoutes(): ResultOrMessage<List<Route>> {
        val reader = DbEntityReader(
            database = database,
            table = RoutesTable,
            entityClass = RouteEntity,
            entityParser = RouteEntityParser,
        )
        return reader.read()
    }

    /**
     * Reads groups from [GroupsTable] in [database]
     * based on knowledge of [routes] and [competitionYear].
     */
    fun readGroups(routes: List<Route>, competitionYear: Int): ResultOrMessage<List<Group>> {
        val parser = GroupEntityParser(competitionYear, routes)
        val reader = DbEntityReader(
            database = database,
            table = GroupsTable,
            entityClass = GroupEntity,
            entityParser = parser,
        )
        return reader.read()
    }

    /**
     * Reads [Competition]
     * from tables [CompetitionHeaderTable], [RoutesTable] and [GroupsTable]
     * in [database].
     */
    fun readCompetition(): ResultOrMessage<Competition> {
         return binding {
             val header = readHeader().bind()
             val routes = readRoutes().bind()
             val groups = readGroups(routes, header.year).bind()
             Competition(
                 discipline = header.discipline,
                 name = header.name,
                 year = header.year,
                 date = header.date,
                 groups = groups,
                 routes = routes,
             )
        }
    }
}