package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.sql.Database
import ru.emkn.kotlin.sms.*
import com.github.michaelbull.result.*
import ru.emkn.kotlin.sms.db.schema.GroupEntity
import ru.emkn.kotlin.sms.db.schema.GroupsTable
import ru.emkn.kotlin.sms.db.util.loggingTransaction


/**
 * Reads [Competition] from multiple tables in [database].
 */
class CompetitionDbReader(
    private val database: Database
) {
    /**
     * Reads groups from [GroupsTable] in [database]
     * based on knowledge of [routes] and [competitionYear].
     */
    fun readGroups(routes: List<Route>, competitionYear: Int): ResultOrMessage<List<Group>> {
        return loggingTransaction(database) {
            runCatching {
                GroupEntity.all().map { entity ->
                    val route = routes.find { it.name == entity.route } ?: return@loggingTransaction Err(
                        "Group \"${entity.label}\" has invalid route name \"${entity.route}\"."
                    )

                    assert(entity.type == GroupType.AGE) // currently, there is only one type
                    val ageFrom = entity.ageFrom ?: return@loggingTransaction Err(
                        "Age group \"${entity.label}\" has a NULL in \"age_from\" column."
                    )
                    val ageTo = entity.ageTo ?: return@loggingTransaction Err(
                        "Age group \"${entity.label}\" has a NULL in \"age_to\" column."
                    )
                    AgeGroup(
                        label = entity.label,
                        route = route,
                        ageFrom = ageFrom,
                        ageTo = ageTo,
                        competitionYear = competitionYear,
                    )
                }
            }.mapDBReadErrorMessage()
        }
    }
}