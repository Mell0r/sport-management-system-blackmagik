package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.sql.Database
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.db.schema.CompetitionHeaderTable
import ru.emkn.kotlin.sms.db.schema.GroupsTable
import ru.emkn.kotlin.sms.db.schema.RoutesTable
import ru.emkn.kotlin.sms.db.util.DbWriter

/**
 * Writes [Competition] to multiple tables in [database].
 */
class CompetitionDbWriter(
    private val database: Database,
    private val competition: Competition,
) {
    private val routesWriter = DbWriter(database, RoutesTable)
    private val groupsWriter = DbWriter(database, GroupsTable)
    private val headerWriter = DbWriter(database, CompetitionHeaderTable)

    /**
     * OVERWRITES the whole [CompetitionHeaderTable].
     */
    fun writeHeader() {
        val header = CompetitionHeader.fromCompetition(competition)
        headerWriter.overwrite(listOf(header))
    }

    /**
     * OVERWRITES the whole [RoutesTable].
     */
    fun writeRoutes() {
        routesWriter.overwrite(competition.routes)
    }

    /**
     * OVERWRITES the whole [GroupsTable].
     */
    fun writeGroups() {
        groupsWriter.overwrite(competition.groups)
    }

    /**
     * OVERWRITES tables [CompetitionHeaderTable], [RoutesTable], [GroupsTable].
     */
    fun writeCompetition() {
        writeHeader()
        writeRoutes()
        writeGroups()
    }
}