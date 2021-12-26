package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.sql.statements.InsertStatement
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.db.schema.CompetitionHeaderTable
import ru.emkn.kotlin.sms.db.writers.RecordableToTableRow

/**
 * A class which stores header data of [Competition].
 *
 * It might be a good idea to later make it so that
 * [Competition] is composed of [CompetitionHeader], route list and group list.
 */
data class CompetitionHeader(
    val discipline: String,
    val name: String,
    val year: Int,
    val date: String,
) : RecordableToTableRow<CompetitionHeaderTable> {
    companion object {
        fun fromCompetition(competition: Competition) = CompetitionHeader(
            discipline = competition.discipline,
            name = competition.name,
            year = competition.year,
            date = competition.date,
        )
    }

    override fun CompetitionHeaderTable.initializeTableRow(statement: InsertStatement<Number>) {
        statement[discipline] = this@CompetitionHeader.discipline
        statement[name] = this@CompetitionHeader.name
        statement[year] = this@CompetitionHeader.year
        statement[date] = this@CompetitionHeader.date
    }
}