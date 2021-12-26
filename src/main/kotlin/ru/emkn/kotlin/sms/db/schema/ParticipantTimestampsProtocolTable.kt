package ru.emkn.kotlin.sms.db.schema

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.db.util.MAX_DB_ROW_LABEL_SIZE
import ru.emkn.kotlin.sms.db.util.TimeColumnType
import ru.emkn.kotlin.sms.time.Time

class ParticipantTimestampsProtocolTable(
    val participant: Participant,
    tableName: String,
) : Table(tableName) {
    val checkpoint = varchar("checkpoint", MAX_DB_ROW_LABEL_SIZE)
    val time: Column<Time> = ParticipantsListTable.registerColumn("starting_time", TimeColumnType())
}