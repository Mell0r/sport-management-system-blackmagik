package ru.emkn.kotlin.sms.db.schema

import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.*
import ru.emkn.kotlin.sms.ParticipantsList
import ru.emkn.kotlin.sms.db.MAX_DB_ROW_LABEL_SIZE
import ru.emkn.kotlin.sms.db.util.TimeColumnType
import ru.emkn.kotlin.sms.time.Time

/**
 * A [ParticipantsList] adapter
 * as org.jetbrains.exposed DSL representative of a DB table.
 */
object ParticipantsListTable : IntIdTable("participants_list") {
    // id is assumed (IntIdTable)
    val age: Column<Int> = integer("age")
    val name: Column<String> = text("name")
    val lastName: Column<String> = text("last_name")
    val group = reference("group", GroupsTable)
    val team: Column<String> = varchar("team", MAX_DB_ROW_LABEL_SIZE) // TODO: Team class and reference to teams table
    val sportsCategory: Column<String> = text("sports_category")
    val startingTime: Column<Time> = registerColumn("starting_time", TimeColumnType())
}