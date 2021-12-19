package ru.emkn.kotlin.sms.io

import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.TestDataSetCompetition1
import ru.emkn.kotlin.sms.competitionEquals
import java.io.File
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
        val dirPath = "$testDataDir/testCompetition-${competition.name}"
        saveCompetition(competition, dirPath)
        val (loadedCompetition, _) = initializeCompetition(dirPath)
        assertNotNull(loadedCompetition)
        assertTrue(competitionEquals(competition, loadedCompetition))
    }

    @Test
    fun `Save and then initialize competition tests`() {
        testCompetitions.forEach { competition ->
            testSingleCompetition(competition)
        }
    }
}