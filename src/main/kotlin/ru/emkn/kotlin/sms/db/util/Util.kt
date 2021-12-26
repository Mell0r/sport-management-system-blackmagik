package ru.emkn.kotlin.sms.db.util

import org.jetbrains.exposed.sql.Database
import ru.emkn.kotlin.sms.ResultOrMessage
import com.github.michaelbull.result.*
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.db.CompetitionHeader
import ru.emkn.kotlin.sms.db.util.TinyLogDebugSqlLogger

/**
 * Maximum allowed label sizes
 * for groups, routes, teams and checkpoints.
 */
const val MAX_DB_ROW_LABEL_SIZE = 64

/**
 * Tries to connect to a database at [url].
 * @returns [Err] if the connection failed.
 */
fun Database.Companion.safeConnectToURL(url: String): ResultOrMessage<Database> {
    return runCatching {
        connect(url, driver = "org.h2.Driver")
    }.mapError { error ->
        "Connection to database at URL \"$url\" failed. Following exception occurred:\n${error.message}"
    }
}

/**
 * Tries to connect to a database at [path].
 * @returns [Err] if the connection failed.
 */
fun Database.Companion.safeConnectToPath(path: String): ResultOrMessage<Database> {
    return runCatching {
        connect("jdbc:h2:$path", driver = "org.h2.Driver")
    }.mapError { error ->
        "Connection to database at \"$path\" failed. Following exception occurred:\n${error.message}"
    }
}


/**
 * Initializes a [transaction] and adds [TinyLogDebugSqlLogger].
 */
fun <T> loggingTransaction(db: Database? = null, statement: Transaction.() -> T): T {
    return transaction(db) {
        addLogger(TinyLogDebugSqlLogger)
        this.statement()
    }
}

inline fun <reified E : Enum<E>> enumTypeToSqlType(): String {
    val stringValues = enumValues<E>().joinToString(", ") {
        "'${it.name}'"
    }
    return "ENUM($stringValues)"
}

inline fun <reified E : Enum<E>> Table.standardCustomEnumeration(name: String) = customEnumeration(
    name = name,
    sql = enumTypeToSqlType<E>(),
    fromDb = { value ->
        enumValueOf<E>(value.toString())
    },
    toDb = { it.name },
)
