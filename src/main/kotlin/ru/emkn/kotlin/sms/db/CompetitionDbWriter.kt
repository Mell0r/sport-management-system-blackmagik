package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import ru.emkn.kotlin.sms.Competition

class CompetitionDbWriter(
    private val database: Database,
    private val competition: Competition,
) {
    /**
     * OVERWRITES the whole [GroupsTable].
     */
    fun writeGroups() {
        return loggingTransaction(database) {
            SchemaUtils.create(GroupsTable) // create if not exists
            GroupsTable.deleteAll()
            competition.groups.forEach { group ->
                with (group) {
                    GroupEntity.new {
                        initializeEntity()
                    }
                }
            }
        }
    }
}