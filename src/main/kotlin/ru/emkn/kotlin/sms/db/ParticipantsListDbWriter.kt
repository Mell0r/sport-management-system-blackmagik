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
    // unsafe?
    // should never throw anything afaik
    fun write(participantsList: ParticipantsList) {
        return loggingTransaction(database) {
            SchemaUtils.create(ParticipantsListTable) // create if not exists
            ParticipantsListTable.deleteAll()
            participantsList.list.forEach { participant ->
                ParticipantEntity.new(participant.id) {
                    age = participant.age
                    name = participant.name
                    lastName = participant.lastName
                    group = participant.group.label
                    team = participant.team
                    sportsCategory = participant.sportsCategory
                    startingTime = participant.startingTime.toString()
                }
            }
        }
    }
}