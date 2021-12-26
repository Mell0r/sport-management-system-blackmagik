package ru.emkn.kotlin.sms.db.parsers

import org.jetbrains.exposed.sql.ResultRow
import ru.emkn.kotlin.sms.ResultOrMessage

/**
 * An object which can parse an object of type [V]
 * from [ResultRow].
 */
interface ResultRowParser<V> {
    fun parse(resultRow: ResultRow): ResultOrMessage<V>
}