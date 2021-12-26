package ru.emkn.kotlin.sms.db.parsers

import org.jetbrains.exposed.sql.ResultRow
import ru.emkn.kotlin.sms.ResultOrMessage
import ru.emkn.kotlin.sms.db.CompetitionHeader
import ru.emkn.kotlin.sms.db.schema.CompetitionHeaderTable
import ru.emkn.kotlin.sms.mapDBReadErrorMessage
import com.github.michaelbull.result.*

object CompetitionHeaderResultRowParser : ResultRowParser<CompetitionHeader> {
    override fun parse(resultRow: ResultRow): ResultOrMessage<CompetitionHeader> {
        return runCatching {
            CompetitionHeader(
                discipline = resultRow[CompetitionHeaderTable.discipline],
                name = resultRow[CompetitionHeaderTable.name],
                year = resultRow[CompetitionHeaderTable.year],
                date = resultRow[CompetitionHeaderTable.date],
            )
        }.mapDBReadErrorMessage()
    }
}