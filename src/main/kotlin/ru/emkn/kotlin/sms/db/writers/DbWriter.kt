package ru.emkn.kotlin.sms.db.writers

import org.jetbrains.exposed.sql.*
import ru.emkn.kotlin.sms.db.util.loggingTransaction

/**
 * An object which writes
 * objects implementing [RecordableToTableRow]
 * to SQL [table] of type [T].
 */
class DbWriter<T : Table>(
    private val database: Database,
    private val table: T,
) {
    fun overwrite(list: List<RecordableToTableRow<T>>) {
        return loggingTransaction(database) {
            SchemaUtils.create(table) // create if not exists
            table.deleteAll()
            list.forEach { recordable ->
                with(recordable) {
                    table.insert { statement ->
                        initializeTableRow(statement)
                    }
                }
            }
        }
    }
}