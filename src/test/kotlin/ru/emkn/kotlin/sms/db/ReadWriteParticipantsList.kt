package ru.emkn.kotlin.sms.db

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.unwrap
import ru.emkn.kotlin.sms.db.readers.ParticipantsListDbReader
import ru.emkn.kotlin.sms.db.writers.ParticipantsListDbWriter
import kotlin.test.*

internal class ReadWriteParticipantsList {
    private val testDbApi : TestDbApi = TestDbApi1
    private val testDataSet = TableTestDataSet1

    private val testCompetition = testDataSet.competition
    private val testParticipantsLists = testDataSet.participantsLists

    @Test
    fun `ParticipantsListDb Reader and Writer correctness test`() {
        val db = testDbApi.connectDB()
        testDataSet.writeAllGroups(db)
        val reader = ParticipantsListDbReader(db, testCompetition)
        val writer = ParticipantsListDbWriter(db)
        testParticipantsLists.forEach { participantsList ->
            writer.overwrite(participantsList)
            val result = reader.read().unwrap()
            assertEquals(participantsList, result)
        }
    }

    @Test
    fun `ParticipantsListDbReader table not exists`() {
        val db = testDbApi.connectDB()
        val reader = ParticipantsListDbReader(db, testCompetition)
        val result = reader.read()
        assertIs<Err<String?>>(result)
    }

    @AfterTest
    @BeforeTest
    fun clearAll() {
        testDataSet.clearAll(testDbApi)
    }
}