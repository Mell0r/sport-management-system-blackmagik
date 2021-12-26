package ru.emkn.kotlin.sms.db

import com.github.michaelbull.result.unwrap
import kotlin.test.*

internal class ReadWriteCompetitionHeaderTest {
    private val testDbApi : TestDbApi = TestDbApi1
    private val testDataSet = TableTestDataSet1

    private val testCompetition = testDataSet.competition

    @Test
    fun `CompetitionDbReader(Writer) read & write competition header correctness`() {
        val db = testDbApi.connectDB()
        testDataSet.writeCompetitionHeader(db)
        val reader = CompetitionDbReader(db)
        val readHeader = reader.readHeader().unwrap()
        val expectedHeader = CompetitionHeader.fromCompetition(testCompetition)
        assertEquals(expectedHeader, readHeader)
    }

    @AfterTest
    @BeforeTest
    fun clearAll() {
        testDataSet.clearAll(testDbApi)
    }
}