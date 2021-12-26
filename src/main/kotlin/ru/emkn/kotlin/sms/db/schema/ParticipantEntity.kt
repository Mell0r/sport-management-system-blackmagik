package ru.emkn.kotlin.sms.db.schema

import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.time.Time

/**
 * A [Participant] adapter
 * to org.jetbrains.exposed DAO entity instance
 * of [ParticipantsListTable] table.
 */
class ParticipantEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ParticipantEntity>(ParticipantsListTable)
    // id is assumed
    var age: Int by ParticipantsListTable.age
    var name: String by ParticipantsListTable.name
    var lastName: String by ParticipantsListTable.lastName
    var group: GroupEntity by GroupEntity referencedOn ParticipantsListTable.group
    var team: String by ParticipantsListTable.team
    var sportsCategory: String by ParticipantsListTable.sportsCategory
    var startingTime: Time by ParticipantsListTable.startingTime

    fun toShortString() = "Participant #$id $name $lastName"
}