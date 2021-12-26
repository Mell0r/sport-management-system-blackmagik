package ru.emkn.kotlin.sms.db.schema

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.id.EntityID
import ru.emkn.kotlin.sms.Route
import ru.emkn.kotlin.sms.RouteType
import ru.emkn.kotlin.sms.db.util.StringEntityClass

/**
 * A [Route] adapter
 * to org.jetbrains.exposed DAO entity instance
 * of [RoutesTable] table.
 */
class RouteEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : StringEntityClass<RouteEntity>(RoutesTable)

    val name: String
        get() = id.value
    val type: RouteType by RoutesTable.type
    val commaSeparatedCheckpoints: String by RoutesTable.commaSeparatedCheckpoints
    val threshold: Int? by RoutesTable.threshold
}