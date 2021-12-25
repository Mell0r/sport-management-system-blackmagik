package ru.emkn.kotlin.sms.db

import com.github.michaelbull.result.*
import org.jetbrains.exposed.sql.Database
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.time.Time

/**
 * Reads [ParticipantsList] from [database],
 * from table that corresponds to [ParticipantsListTable].
 */
class ParticipantsListDbReader(
    private val database: Database,
    private val competition: Competition,
) {

    fun read(): ResultOrMessage<ParticipantsList> {
        return loggingTransaction(database) {
            runCatching {
                val participants = ParticipantEntity.all().map { entity ->
                    val group = competition.getGroupByLabelOrNull(entity.group) ?: return@loggingTransaction Err(
                        "${entity.toShortString()} has invalid group label \"${entity.group}\""
                    )
                    val kek = entity.startingTime
                    Participant(
                        id = entity.id.value,
                        age = entity.age,
                        name = entity.name,
                        lastName = entity.lastName,
                        group = group,
                        team = entity.team,
                        sportsCategory = entity.sportsCategory,
                        startingTime = kek,
                    )
                }
                val participantsList = ParticipantsList(participants)
                participantsList
            }.mapError { exception ->
                "Some error happened while reading database:\n${exception.message}"
            }
        }
    }
}