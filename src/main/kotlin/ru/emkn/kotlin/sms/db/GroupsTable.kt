package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import ru.emkn.kotlin.sms.*

/**
 * A [org.jetbrains.exposed] table representative,
 * storing [Group]s.
 */
object GroupsTable : IntIdTable("groups") {
    val label: Column<String> = varchar("label", MAX_DB_ROW_LABEL_SIZE)
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