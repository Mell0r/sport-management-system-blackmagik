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
    /**
     * OVERWRITES the whole [ParticipantsListTable].
     */
    // unsafe?
    // should never throw anything afaik
    fun write(participantsList: ParticipantsList) {
        loggingTransaction(database) {
            SchemaUtils.create(ParticipantsListTable) // create if not exists
            ParticipantsListTable.deleteAll()
            participantsList.list.forEach { participant ->
                participant.toEntity()
            }
        }
    }
}