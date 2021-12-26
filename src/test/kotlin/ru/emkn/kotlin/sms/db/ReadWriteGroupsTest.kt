package ru.emkn.kotlin.sms.db

import com.github.michaelbull.result.unwrap
import ru.emkn.kotlin.sms.groupListsEquals
import kotlin.test.*

internal class ReadWriteGroupsTest {
    private val testDbApi = TestDbApi1
    private val testDataSet = TableTestDataSet1

    private val testCompetition = testDataSet.competition
    private val testGroups = testCompetition.groups
    private val testRoutes = testCompetition.routes

    @Test
    fun `CompetitionDbReader(Writer) read & write groups correctness`() {
        val db = testDbApi.connectDB()
        testDataSet.writeAllGroups(db)
        val reader = CompetitionDbReader(db)
        val readGroups = reader.readGroups(testRoutes, testCompetition.year).unwrap()
        assertTrue {
            groupListsEquals(testGroups, readGroups)
        }
    }

    @AfterTest
    @BeforeTest
    fun clearAll() {
        testDataSet.clearAll(testDbApi)
    }
}