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
    var age by ParticipantsListTable.age
    var name by ParticipantsListTable.name
    var lastName by ParticipantsListTable.lastName
    var group by ParticipantsListTable.group
    var team by ParticipantsListTable.team
    var sportsCategory by ParticipantsListTable.sportsCategory
    var startingTime by ParticipantsListTable.startingTime

    fun toShortString() = "Participant #$id $name $lastName"
}