package ru.emkn.kotlin.sms.io

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.*
import java.io.File
import kotlin.test.Test
import kotlin.test.*

internal class InitializeSaveCompetitionTests {
    /* Test data */
    private val testCompetitions = TestDataSetCompetition1.testCompetitions

    /* File system */
    private val testDataDir = "test-data/io/competition"

    @BeforeTest
    fun prepareDirectories() {
        File(testDataDir).mkdirs()
    }

    @AfterTest
    fun clearTestDir() {
        File(testDataDir).deleteRecursively()
    }

    private fun testSingleCompetition(competition: Competition) {
        val dir = "$testDataDir/testCompetition-${competition.name}"
        saveCompetition(competition, dir)
        val loadedCompetition = initializeCompetition(dir)
        assertTrue(competitionEquals(competition, loadedCompetition))
    }

    @Test
    fun `Save and then initialize competition tests`() {
        testCompetitions.forEach { competition ->
            testSingleCompetition(competition)
        }
    }
}