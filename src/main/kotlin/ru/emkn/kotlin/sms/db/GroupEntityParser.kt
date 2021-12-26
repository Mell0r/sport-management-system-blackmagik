package ru.emkn.kotlin.sms.db

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.db.schema.GroupEntity
import ru.emkn.kotlin.sms.db.util.EntityParser

class GroupEntityParser(
    private val competitionYear: Int,
    private val routes: List<Route>,
) : EntityParser<String, GroupEntity, Group> {
    override fun parse(entity: GroupEntity): ResultOrMessage<Group> {
        val route = routes.find { it.name == entity.route } ?: return Err(
            "Group \"${entity.label}\" has invalid route name \"${entity.route}\"."
        )

        assert(entity.type == GroupType.AGE) // currently, there is only one type
        val ageFrom = entity.ageFrom ?: return Err(
            "Age group \"${entity.label}\" has a NULL in \"age_from\" column."
        )
        val ageTo = entity.ageTo ?: return Err(
            "Age group \"${entity.label}\" has a NULL in \"age_to\" column."
        )
        return Ok(
            AgeGroup(
                label = entity.label,
                route = route,
                ageFrom = ageFrom,
                ageTo = ageTo,
                competitionYear = competitionYear,
            )
        )
    }
}