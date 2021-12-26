package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.sql.Database
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.db.schema.ParticipantsListTable
import ru.emkn.kotlin.sms.db.util.DbWriter

/**
 * Writes [ParticipantsList] to [database],
 * overwriting all the existing data,
 * to table that corresponds to [ParticipantsListTable].
 */
class ParticipantsListDbWriter(
    private val database: Database,
) {
    private val writer = DbWriter(database, ParticipantsListTable)

    fun overwrite(participantsList: ParticipantsList) {
        writer.overwrite(participantsList.list)
    }
}