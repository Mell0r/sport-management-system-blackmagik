package ru.emkn.kotlin.sms.db.readers

import com.github.michaelbull.result.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.ResultRow
import ru.emkn.kotlin.sms.ResultOrMessage
import ru.emkn.kotlin.sms.db.parsers.ResultRowParser
import ru.emkn.kotlin.sms.db.util.loggingTransaction
import com.github.michaelbull.result.runCatching
import org.jetbrains.exposed.sql.selectAll
import ru.emkn.kotlin.sms.mapDBReadErrorMessage
import ru.emkn.kotlin.sms.successOrNothing

/**
 * Reads objects of type [V]
 * from [ResultRow]s in [table] in [database]
 * via [resultRowParser].
 */
class DbResultRowReader<V>(
    private val database: Database,
    private val table: Table,
    private val resultRowParser: ResultRowParser<V>,
) {
    fun readAll(): ResultOrMessage<List<V>> {
        return loggingTransaction(database) {
            runCatching {
                binding<List<V>, String?> {
                    table.selectAll().map { resultRow ->
                        resultRowParser.parse(resultRow).bind()
                    }
                }.successOrNothing {
                    return@loggingTransaction Err(it)
                }
            }.mapDBReadErrorMessage()
        }
    }

    fun readFirst(): ResultOrMessage<V> {
        return loggingTransaction(database) {
            runCatching {
                val resultRow = try {
                    table.selectAll().limit(1).first()
                } catch (e: NoSuchElementException) {
                    return@loggingTransaction Err("Cannot read first row: table ${table.tableName} is empty!")
                }
                resultRowParser.parse(resultRow).successOrNothing {
                    return@loggingTransaction Err(it)
                }
            }.mapDBReadErrorMessage()
        }
    }
}