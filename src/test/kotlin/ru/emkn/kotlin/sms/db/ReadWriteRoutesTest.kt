package ru.emkn.kotlin.sms.db

import com.github.michaelbull.result.unwrap
import ru.emkn.kotlin.sms.db.readers.CompetitionDbReader
import kotlin.test.*

internal class ReadWriteRoutesTest {
    private val testDbApi = TestDbApi1
    private val testDataSet = TableTestDataSet1

    private val testCompetition = testDataSet.competition
    private val testRoutes = testCompetition.routes

    @Test
    fun `CompetitionDbReader(Writer) read & write routes correctness`() {
        val db = testDbApi.connectDB()
        testDataSet.writeAllRoutes(db)
        val reader = CompetitionDbReader(db)
        val readRoutes = reader.readRoutes().unwrap()
        assertEquals(
            testRoutes.toSet(),
            readRoutes.toSet(),
        )
    }

    @AfterTest
    @BeforeTest
    fun clearAll() {
        testDataSet.clearAll(testDbApi)
    }
}