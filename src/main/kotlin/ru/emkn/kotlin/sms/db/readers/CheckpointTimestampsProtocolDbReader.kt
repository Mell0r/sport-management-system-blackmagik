package ru.emkn.kotlin.sms.db.readers

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import org.jetbrains.exposed.sql.Database
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.db.parsers.ParticipantIdAndTimeResultRowParser
import ru.emkn.kotlin.sms.db.schema.CheckpointTimestampsProtocolTable
import ru.emkn.kotlin.sms.results_processing.CheckpointTimestampsProtocol

/**
 * Reads a [CheckpointTimestampsProtocol]
 * from [table] in [database].
 */
class CheckpointTimestampsProtocolDbReader(
    private val database: Database,
    private val table: CheckpointTimestampsProtocolTable,
    private val checkpointLabel: CheckpointLabelT,
) {
    private val rowParser = ParticipantIdAndTimeResultRowParser(table)

    fun read(): ResultOrMessage<CheckpointTimestampsProtocol> {
        val reader = DbResultRowReader(
            database = database,
            table = table,
            resultRowParser = rowParser,
        )
        val participantTimes = reader.readAll().successOrNothing { return Err(it) }
        return Ok(CheckpointTimestampsProtocol(checkpointLabel, participantTimes))
    }
}