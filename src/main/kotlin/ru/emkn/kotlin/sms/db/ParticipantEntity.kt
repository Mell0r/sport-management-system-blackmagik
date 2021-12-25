package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import ru.emkn.kotlin.sms.Participant

/**
 * A [Participant] adapter
 * to org.jetbrains.exposed DAO entity instance
 * of [ParticipantsListTable] table.
 */
class ParticipantEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ParticipantEntity>(ParticipantsListTable)
    // id is assumed
    val age by ParticipantsListTable.age
    val name by ParticipantsListTable.name
    val lastName by ParticipantsListTable.lastName
    val group by ParticipantsListTable.group
    val team by ParticipantsListTable.team
    val sportsCategory by ParticipantsListTable.sportsCategory
    val startingTime by ParticipantsListTable.startingTime
}