package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll

/**
 * An object, which
 * converts objects implementing [ConvertibleToEntity]
 * to DB entities of type [T],
 * and then writes them into [database].
 */
class DbWriter<ID : Comparable<ID>, T : Entity<ID>>(
    private val database: Database,
    private val table: Table,
) {
    fun overwrite(list: List<ConvertibleToEntity<ID, T>>) {
        return loggingTransaction(database) {
            SchemaUtils.create(table) // create if not exists
            table.deleteAll()
            list.forEach { convertible ->
                convertible.toEntity()
            }
        }
    }
}