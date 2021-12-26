package ru.emkn.kotlin.sms.db.schema

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ru.emkn.kotlin.sms.CheckpointLabelT
import ru.emkn.kotlin.sms.db.util.TimeColumnType
import ru.emkn.kotlin.sms.time.Time

class CheckpointTimestampsProtocolTable(
    val checkpointLabel: CheckpointLabelT,
    tableName: String,
) : Table(tableName) {
    val participantID = reference("participant_id", ParticipantsListTable)
    val time: Column<Time> = ParticipantsListTable.registerColumn("starting_time", TimeColumnType())
}