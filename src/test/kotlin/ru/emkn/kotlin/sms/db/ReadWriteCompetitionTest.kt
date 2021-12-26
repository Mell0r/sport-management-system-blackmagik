package ru.emkn.kotlin.sms.db

import com.github.michaelbull.result.unwrap
import ru.emkn.kotlin.sms.competitionEquals
import ru.emkn.kotlin.sms.db.readers.CompetitionDbReader
import kotlin.test.*

internal class ReadWriteCompetitionTest {
    private val testDbApi : TestDbApi = TestDbApi1
    private val testDataSet = TableTestDataSet1

    @Test
    fun `CompetitionDbReader(Writer) read & write competition correctness`() {
        val db = testDbApi.connectDB()
        testDataSet.writeCompetition(db)
        val reader = CompetitionDbReader(db)
        val readCompetition = reader.readCompetition().unwrap()
        assertTrue { competitionEquals(testDataSet.competition, readCompetition) }
    }

    @AfterTest
    @BeforeTest
    fun clearAll() {
        testDataSet.clearAll(testDbApi)
    }
}