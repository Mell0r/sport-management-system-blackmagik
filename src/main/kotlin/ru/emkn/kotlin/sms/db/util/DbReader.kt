package ru.emkn.kotlin.sms.db.util

import com.github.michaelbull.result.*
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import ru.emkn.kotlin.sms.ResultOrMessage
import ru.emkn.kotlin.sms.mapDBReadErrorMessage
import ru.emkn.kotlin.sms.successOrNothing

/**
 * Ab object which can read objects of type [V]
 * from [database] from [table] of type [T],
 * containing entities of type [E],
 * via [entityParser].
 */
class DbReader<T : Table, ID : Comparable<ID>, E : Entity<ID>, V>(
    private val database: Database,
    private val table: T,
    private val entityClass: EntityClass<ID, E>,
    private val entityParser: EntityParser<ID, E, V>,
) {
    fun read(): ResultOrMessage<List<V>> {
        return loggingTransaction {
            runCatching {
                binding<List<V>, String?> {
                    entityClass.all().map { entity ->
                        entityParser.parse(entity).bind()
                    }
                }.successOrNothing {
                    return@loggingTransaction Err(it)
                }
            }.mapDBReadErrorMessage()
        }
    }
}