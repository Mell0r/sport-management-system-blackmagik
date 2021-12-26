package ru.emkn.kotlin.sms.db

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.ResultOrMessage
import ru.emkn.kotlin.sms.db.schema.ParticipantEntity
import ru.emkn.kotlin.sms.db.util.EntityParser

/**
 * Parses a [Participant] from [ParticipantEntity]
 * via [competition].
 */
class ParticipantEntityParser(
    private val competition: Competition,
) : EntityParser<Int, ParticipantEntity, Participant> {
    override fun parse(entity: ParticipantEntity): ResultOrMessage<Participant> {
        val group = competition.getGroupByLabelOrNull(entity.group.label) ?: return Err(
            "${entity.toShortString()} has invalid group label \"${entity.group}\""
        )
        return Ok(
            Participant(
                id = entity.id.value,
                age = entity.age,
                name = entity.name,
                lastName = entity.lastName,
                group = group,
                team = entity.team,
                sportsCategory = entity.sportsCategory,
                startingTime = entity.startingTime,
            )
        )
    }
}
