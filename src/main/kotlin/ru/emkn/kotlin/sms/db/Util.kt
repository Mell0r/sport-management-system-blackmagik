package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.sql.Database
import ru.emkn.kotlin.sms.ResultOrMessage
import com.github.michaelbull.result.*
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Maximum allowed label sizes
 * for groups, routes and teams.
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
fun <T> loggingTransaction(db: Database? = null, statement: Transaction.() -> T) {
    return transaction(db) {
        addLogger(TinyLogDebugSqlLogger)
        this.statement()
    }
}