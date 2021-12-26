package ru.emkn.kotlin.sms.db.readers

import com.github.michaelbull.result.*
import org.jetbrains.exposed.sql.Database
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.db.parsers.ParticipantEntityParser
import ru.emkn.kotlin.sms.db.schema.ParticipantEntity
import ru.emkn.kotlin.sms.db.schema.ParticipantsListTable

/**
 * Reads [ParticipantsList] from [database],
 * from table that corresponds to [ParticipantsListTable].
 */
class ParticipantsListDbReader(
    private val database: Database,
    competition: Competition,
) {
    private val participantEntityParser = ParticipantEntityParser(competition)

    private val reader = DbEntityReader(
        database = database,
        table = ParticipantsListTable,
        entityClass = ParticipantEntity,
        entityParser = participantEntityParser,
    )

    fun read(): ResultOrMessage<ParticipantsList> {
        return reader.read().map { ParticipantsList(it) }
    }
}