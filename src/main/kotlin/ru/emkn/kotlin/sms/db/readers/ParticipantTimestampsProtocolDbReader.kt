package ru.emkn.kotlin.sms.db.readers

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import org.jetbrains.exposed.sql.Database
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.db.parsers.CheckpointAndTimeResultRowParser
import ru.emkn.kotlin.sms.db.schema.ParticipantTimestampsProtocolTable
import ru.emkn.kotlin.sms.results_processing.ParticipantTimestampsProtocol

/**
 * Reads a [ParticipantTimestampsProtocol]
 * from [table] in [database].
 */
class ParticipantTimestampsProtocolDbReader(
    private val database: Database,
    private val table: ParticipantTimestampsProtocolTable,
    private val participantID: Int,
) {
    private val rowParser = CheckpointAndTimeResultRowParser(table)

    fun read(): ResultOrMessage<ParticipantTimestampsProtocol> {
        val reader = DbResultRowReader(
            database = database,
            table = table,
            resultRowParser = rowParser,
        )
        val checkpointTimes = reader.readAll().successOrNothing { return Err(it) }
        return Ok(ParticipantTimestampsProtocol(participantID, checkpointTimes))
    }
}