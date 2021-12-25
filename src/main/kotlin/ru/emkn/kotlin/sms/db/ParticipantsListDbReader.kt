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
            val participants = ParticipantEntity.all().map { entity ->
                val group = competition.getGroupByLabelOrNull(entity.group) ?: return@loggingTransaction Err(
                    "${entity.toShortString()} has invalid group label \"${entity.group}\""
                )
                val startingTime = runCatching {
                    Time.fromString(entity.startingTime)
                }.successOrNothing { exception ->
                    when (exception) {
                        is IllegalArgumentException -> return@loggingTransaction Err(
                            "${entity.toShortString()} has invalid starting time \"${entity.startingTime}\":\n${exception.message}"
                        )
                        else -> throw exception
                    }
                }
                Participant(
                    id = entity.id.value,
                    age = entity.age,
                    name = entity.name,
                    lastName = entity.lastName,
                    group = group,
                    team = entity.team,
                    sportsCategory = entity.sportsCategory,
                    startingTime = startingTime,
                )
            }
            val participantsList = ParticipantsList(participants)
            Ok(participantsList)
        }
    }
}