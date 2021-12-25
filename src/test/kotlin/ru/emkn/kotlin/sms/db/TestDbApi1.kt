package ru.emkn.kotlin.sms.db

import com.github.michaelbull.result.unwrap
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.exists

object TestDbApi1 : TestDbApi {
    private const val testDBDir = "test-data/db/db-test-1"
    private const val testDBName = "sms-test"
    private const val testDBPath = "$testDBDir/$testDBName"

    override fun connectDB() = Database.safeConnectToPath("./$testDBPath").unwrap()

    override fun clearDB(table: Table) {
        loggingTransaction {
            if (table.exists()) {
                table.deleteAll()
                exec(table.dropStatement().joinToString(" "))
            }
        }
    }
}