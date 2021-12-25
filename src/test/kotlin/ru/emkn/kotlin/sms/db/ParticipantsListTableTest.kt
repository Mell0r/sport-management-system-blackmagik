package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

internal class ParticipantsListTableTest {

    private val testDBDir = "test-data/db/db-test-1"
    private val testDBName = "sms-test"
    private val testDBPath = "$testDBDir/$testDBName"

    private fun ParticipantEntity.toDetailedString() = "$id. $name $lastName (group \"$group\", team \"$team\", sports category \"$sportsCategory\") starts at $startingTime"

    @BeforeTest
    fun initDB() {
        val database = Database.connect("jdbc:h2:./$testDBPath", driver = "org.h2.Driver")
        transaction(database) {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(ParticipantsListTable)
            ParticipantEntity.new {
                age = 25
                name = "Alexey"
                lastName = "Kolotilov"
                group = "group1"
                team = "team1"
                sportsCategory = ""
                startingTime = "00:00:00"
            }
        }
    }

    @Test
    fun `sample participants list table test`() {
        transaction {
            ParticipantEntity.all().forEach {
                println(it.toDetailedString())
            }
        }
    }

    @AfterTest
    fun clearDB() {
        transaction {
            ParticipantsListTable.deleteAll()
            exec(ParticipantsListTable.dropStatement().joinToString(" "))
        }
    }
}