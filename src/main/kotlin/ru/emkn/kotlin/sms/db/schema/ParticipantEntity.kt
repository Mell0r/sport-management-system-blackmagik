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
    val age: Int by ParticipantsListTable.age
    val name: String by ParticipantsListTable.name
    val lastName: String by ParticipantsListTable.lastName
    val group: GroupEntity by GroupEntity referencedOn ParticipantsListTable.group
    val team: String by ParticipantsListTable.team
    val sportsCategory: String by ParticipantsListTable.sportsCategory
    val startingTime: Time by ParticipantsListTable.startingTime

    fun toShortString() = "Participant #$id $name $lastName"
}