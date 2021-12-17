package ru.emkn.kotlin.sms.gui.builders

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.*
import kotlin.test.Test
import kotlin.test.assertTrue

internal class CompetitionBuilderTests {
    /* Test data */
    private val testCompetitions = TestDataSetCompetition1WithoutAtLeastKRoutes.testCompetitions

    @Test
    fun `CompetitionBuilder dot build() tests`() {
        fun singleTest(competition: Competition) {
            val builder = CompetitionBuilder.fromCompetition(competition)
            val builtCompetition = builder.build()
            Logger.debug { "competition = ${competitionToString(competition)}" }
            Logger.debug { "buildCompetition = ${competitionToString(builtCompetition)}" }
            assertTrue(competitionEquals(competition, builtCompetition))
        }
        testCompetitions.forEach { competition ->
            singleTest(competition)
        }
    }
}