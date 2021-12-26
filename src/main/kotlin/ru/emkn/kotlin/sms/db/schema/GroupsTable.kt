package ru.emkn.kotlin.sms.db.schema

import org.jetbrains.exposed.sql.*
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.db.MAX_DB_ROW_LABEL_SIZE
import ru.emkn.kotlin.sms.db.util.StringIdTable

/**
 * A [org.jetbrains.exposed] table representative,
 * storing [Group]s.
 */
object GroupsTable : StringIdTable("groups", "label", MAX_DB_ROW_LABEL_SIZE) {
    val route: Column<String> = varchar("route", MAX_DB_ROW_LABEL_SIZE) // TODO reference to route table
    val type: Column<GroupType> = customEnumeration(
        name = "type",
        sql = GroupType.sqlType,
        fromDb = { value ->
            GroupType.values().find { it.textRepresentation == value }
                ?: throw IllegalArgumentException("Unknown group type \"$value\".")
        },
        toDb = { it.textRepresentation },
    )
    val ageFrom: Column<Int?> = integer("age_from").nullable()
    val ageTo: Column<Int?> = integer("age_to").nullable()
}