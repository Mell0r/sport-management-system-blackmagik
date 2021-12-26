package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import ru.emkn.kotlin.sms.GroupType

class GroupEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : StringEntityClass<GroupEntity>(GroupsTable)

    val label: String
        get() = id.value
    var route: String by GroupsTable.route // TODO reference to route table
    var type: GroupType by GroupsTable.type
    var ageFrom: Int? by GroupsTable.ageFrom
    var ageTo: Int? by GroupsTable.ageTo
}