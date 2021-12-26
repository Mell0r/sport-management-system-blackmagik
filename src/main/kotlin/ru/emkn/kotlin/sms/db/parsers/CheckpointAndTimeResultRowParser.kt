package ru.emkn.kotlin.sms.db.parsers

import org.jetbrains.exposed.sql.ResultRow
import ru.emkn.kotlin.sms.ResultOrMessage
import ru.emkn.kotlin.sms.db.schema.ParticipantTimestampsProtocolTable
import ru.emkn.kotlin.sms.mapDBReadErrorMessage
import ru.emkn.kotlin.sms.results_processing.CheckpointAndTime

/**
 * Parses [CheckpointAndTime] from [ResultRow] of [table].
 */
class CheckpointAndTimeResultRowParser(
    private val table: ParticipantTimestampsProtocolTable,
) : ResultRowParser<CheckpointAndTime> {
    override fun parse(resultRow: ResultRow): ResultOrMessage<CheckpointAndTime> {
        return com.github.michaelbull.result.runCatching {
            CheckpointAndTime(resultRow[table.checkpoint], resultRow[table.time])
        }.mapDBReadErrorMessage()
    }
}