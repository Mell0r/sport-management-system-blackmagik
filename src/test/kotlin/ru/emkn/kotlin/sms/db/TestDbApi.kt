package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table

interface TestDbApi {
    fun connectDB(): Database
    fun clearDB(table: Table)
}