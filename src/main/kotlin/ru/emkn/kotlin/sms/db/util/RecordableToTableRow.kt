package ru.emkn.kotlin.sms.db.util

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.InsertStatement

/**
 * An object which can be converted to a row
 * in an [org.jetbrains.exposed] SQL table representative of type [T].
 */
interface RecordableToTableRow<T : Table> {
    fun T.initializeTableRow(statement: InsertStatement<Number>)
}