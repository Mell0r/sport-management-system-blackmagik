package ru.emkn.kotlin.sms.db

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.unwrap
import org.jetbrains.exposed.sql.*
import kotlin.test.*

internal class ParticipantsListTableTest {

    private val testDBDir = "test-data/db/db-test-1"
    private val testDBName = "sms-test"
    private val testDBPath = "$testDBDir/$testDBName"

    private val testDataSet = TableTestDataSet1
    private val testCompetition = testDataSet.competition
    private val testParticipantsList = testDataSet.participantsLists

    private fun connectDB() = Database.safeConnectToPath("./$testDBPath").unwrap()

    @Test
    fun `ParticipantsListDb Reader and Writer correctness test`() {
        val db = connectDB()
        val reader = ParticipantsListDbReader(db, testCompetition)
        val writer = ParticipantsListDbWriter(db)
        testParticipantsList.forEach { participantsList ->
            writer.write(participantsList)
            val result = reader.read().unwrap()
            assertEquals(participantsList, result)
        }
    }

    @Test
    fun `ParticipantsListDbReader table not exists`() {
        val db = connectDB()
        val reader = ParticipantsListDbReader(db, testCompetition)
        val result = reader.read()
        assertIs<Err<String?>>(result)
    }

    @AfterTest
    fun clearDB() {
        loggingTransaction {
            if (ParticipantsListTable.exists()) {
                ParticipantsListTable.deleteAll()
                exec(ParticipantsListTable.dropStatement().joinToString(" "))
            }
        }
    }
}