package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import ru.emkn.kotlin.sms.Competition

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