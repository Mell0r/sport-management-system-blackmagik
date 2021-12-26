package ru.emkn.kotlin.sms.db.parsers

import org.jetbrains.exposed.sql.ResultRow
import ru.emkn.kotlin.sms.ResultOrMessage
import ru.emkn.kotlin.sms.db.schema.CheckpointTimestampsProtocolTable
import ru.emkn.kotlin.sms.mapDBReadErrorMessage
import ru.emkn.kotlin.sms.results_processing.IdAndTime

/**
 * Parses [IdAndTime] from [ResultRow] of [table].
 */
class ParticipantIdAndTimeResultRowParser(
    private val table: CheckpointTimestampsProtocolTable
) : ResultRowParser<IdAndTime> {
    override fun parse(resultRow: ResultRow): ResultOrMessage<IdAndTime> {
        return com.github.michaelbull.result.runCatching {
            IdAndTime(resultRow[table.participantID].value, resultRow[table.time])
        }.mapDBReadErrorMessage()
    }
}