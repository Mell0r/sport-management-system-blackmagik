package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.sql.Database
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.db.schema.GroupsTable
import ru.emkn.kotlin.sms.db.util.DbWriter

/**
 * Writes [Competition] to multiple tables in [database].
 */
class CompetitionDbWriter(
    private val database: Database,
    private val competition: Competition,
) {
    private val groupsWriter = DbWriter(database, GroupsTable)

    /**
     * OVERWRITES the whole [GroupsTable].
     */
    fun writeGroups() {
        groupsWriter.overwrite(competition.groups)
    }
}