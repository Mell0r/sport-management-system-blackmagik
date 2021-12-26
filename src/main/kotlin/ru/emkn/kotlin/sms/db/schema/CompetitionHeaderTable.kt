package ru.emkn.kotlin.sms.db.schema

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

/**
 * A [org.jetbrains.exposed] table representative,
 * storing competition header data: discipline, name, year, date.
 */
object CompetitionHeaderTable : Table("competition_header") {
    val discipline: Column<String> = text("discipline")
    val name: Column<String> = text("name")
    val year: Column<Int> = integer("year")
    val date: Column<String> = text("date")
}