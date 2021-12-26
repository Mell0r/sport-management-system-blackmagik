package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import ru.emkn.kotlin.sms.*

/**
 * Writes [ParticipantsList] to [database],
 * overwriting all the existing data,
 * to table that corresponds to [ParticipantsListTable].
 */
class ParticipantsListDbWriter(
    private val database: Database,
) {
    private val writer = DbWriter<Int, ParticipantEntity>(database, ParticipantsListTable)

    fun overwrite(participantsList: ParticipantsList) {
        writer.overwrite(participantsList.list)
    }
}