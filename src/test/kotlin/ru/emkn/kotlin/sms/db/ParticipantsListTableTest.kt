package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.sql.*
import ru.emkn.kotlin.sms.successOrNothing
import kotlin.test.*

internal class ParticipantsListTableTest {

    private val testDBDir = "test-data/db/db-test-1"
    private val testDBName = "sms-test"
    private val testDBPath = "$testDBDir/$testDBName"

    private fun ParticipantEntity.toDetailedString() = "$id. $name $lastName (group \"$group\", team \"$team\", sports category \"$sportsCategory\") starts at $startingTime"

    @BeforeTest
    fun initDB() {
        val db = Database.safeConnectToPath("./$testDBPath").successOrNothing {
            throw InternalError("Bad path for test database.")
        }
        loggingTransaction(db) {
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
        loggingTransaction {
            ParticipantEntity.all().forEach {
                println(it.toDetailedString())
            }
        }
    }

    @AfterTest
    fun clearDB() {
        loggingTransaction {
            ParticipantsListTable.deleteAll()
            exec(ParticipantsListTable.dropStatement().joinToString(" "))
        }
    }
}