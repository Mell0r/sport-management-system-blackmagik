package ru.emkn.kotlin.sms.db.util

import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs
import org.tinylog.Logger

object TinyLogDebugSqlLogger : SqlLogger {
    override fun log(context: StatementContext, transaction: Transaction) {
        val logMessage = "SQL: ${context.expandArgs(transaction)}"
        Logger.debug {logMessage}
    }
}