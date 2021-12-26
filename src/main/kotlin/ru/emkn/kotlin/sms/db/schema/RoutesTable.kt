package ru.emkn.kotlin.sms.db.schema

import org.jetbrains.exposed.sql.Column
import ru.emkn.kotlin.sms.Route
import ru.emkn.kotlin.sms.RouteType
import ru.emkn.kotlin.sms.db.util.MAX_DB_ROW_LABEL_SIZE
import ru.emkn.kotlin.sms.db.util.StringIdTable
import ru.emkn.kotlin.sms.db.util.standardCustomEnumeration

/**
 * A [org.jetbrains.exposed] table representative,
 * storing [Route]s.
 */
object RoutesTable : StringIdTable("routes", "name", MAX_DB_ROW_LABEL_SIZE) {
    val type: Column<RouteType> = standardCustomEnumeration("type")
}