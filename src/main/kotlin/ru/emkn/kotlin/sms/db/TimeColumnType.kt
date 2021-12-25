package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.sql.*
import ru.emkn.kotlin.sms.time.*
import java.sql.Clob

/**
 * A custom [org.jetbrains.exposed] [ColumnType] that represents [Time] class.
 * In the database they are stored as varchar([MAX_TIME_STRING_LEN]).
 */
class TimeColumnType : ColumnType() {
    companion object {
        const val MAX_TIME_STRING_LEN = 32
    }

    override fun sqlType() = "VARCHAR(${MAX_TIME_STRING_LEN})"

    override var nullable: Boolean
        get() = false
        set(_) {}

    /**
     * @throws [IllegalArgumentException] if the value is wrong type.
     */
    override fun validateValueBeforeUpdate(value: Any?) {
        require(value is Time)
    }

    // returns garbage if value is bad ??
    override fun valueFromDB(value: Any): Any {
        val stringOrValue = when (value) {
            is Clob -> value.characterStream.readText()
            is ByteArray -> String(value)
            else -> value
        }
        return when (stringOrValue) {
            is String -> Time.fromStringOrNull(stringOrValue) ?: value
            else -> value
        }
    }

    override fun notNullValueToDB(value: Any): Any {
        return when (value) {
            is Time -> value.toString()
            else -> value
        }
    }

    override fun nonNullValueToString(value: Any): String {
        return value.toString()
    }
}