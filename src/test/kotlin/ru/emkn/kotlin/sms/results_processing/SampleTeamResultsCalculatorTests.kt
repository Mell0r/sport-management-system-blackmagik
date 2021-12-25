package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.time.Time
import ru.emkn.kotlin.sms.time.s
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

internal class SampleTeamResultsCalculatorTests {
    private val testRoute = OrderedCheckpointsRoute("", mutableListOf())
    private val groupM12To15 = AgeGroup("M12-15", testRoute, 12, 15, 2021)
    private val groupM19To35 = AgeGroup("M19-35", testRoute, 19, 35, 2021)
    private val groupM16To18 = AgeGroup("M16-18", testRoute, 16, 18, 2021)

    private val p0 = Participant(0, 14, "Name2", "Surname2", groupM12To15, "Team1", "", 0.s())
    private val p1 = Participant(1, 13, "Name1", "Surname1", groupM12To15, "Team1", "", 0.s())
    private val p2 = Participant(2, 30, "Name2", "Surname2", groupM19To35, "Team1", "", 0.s())
    private val p3 = Participant(3, 18, "Name3", "Surname3", groupM16To18, "Team1", "", 0.s())
    private val p4 = Participant(4, 16, "Name4", "Surname4", groupM16To18, "Team1", "", 0.s())

    private val p5 = Participant(5, 21, "Name5", "Surname5", groupM19To35, "Team2", "", 0.s())
    private val p6 = Participant(6, 34, "Name6", "Surname6", groupM19To35, "Team2", "", 0.s())
    private val p7 = Participant(7, 12, "Name7", "Surname7", groupM12To15, "Team2", "", 0.s())

    private val p8 = Participant(8, 15, "Name8", "Surname8", groupM12To15, "Team3", "", 0.s())
    private val p9 = Participant(9, 16, "Name9", "Surname9", groupM16To18, "Team3", "", 0.s())

    private val testGroupResultProtocols = listOf(
        GroupResultProtocol(
            group = groupM12To15,
            entries = listOf(
                ParticipantWithFinalResult(p7, FinalParticipantResult.Finished(Time(500))),
                ParticipantWithFinalResult(p1, FinalParticipantResult.Finished(Time(600))),
                ParticipantWithFinalResult(p0, FinalParticipantResult.Finished(Time(600))),
                ParticipantWithFinalResult(p8, FinalParticipantResult.Disqualified()),
            )
        ),
        GroupResultProtocol(
            group = groupM16To18,
            entries = listOf(
                ParticipantWithFinalResult(p4, FinalParticipantResult.Finished(Time(1500))),
                ParticipantWithFinalResult(p9, FinalParticipantResult.Finished(Time(2000))),
                ParticipantWithFinalResult(p3, FinalParticipantResult.Finished(Time(3000))),
            )
        ),
        GroupResultProtocol(
            group = groupM19To35,
            entries = listOf(
                // whole group was disqualified --- definitely a real situation
                ParticipantWithFinalResult(p2, FinalParticipantResult.Disqualified()),
                ParticipantWithFinalResult(p5, FinalParticipantResult.Disqualified()),
                ParticipantWithFinalResult(p6, FinalParticipantResult.Disqualified()),
            )
        ),
    )

    private val expectedTeamResultsProtocol = TeamResultsProtocol(
        listOf(
            TeamToScore("Team1", 260),
            TeamToScore("Team2", 100),
            TeamToScore("Team3", 67),
        )
    )

    @Test
    fun `Generate team results protocols test`() {
        val teamResultsProtocol = SampleTeamResultsCalculator.calculate(
            groupResultProtocols = testGroupResultProtocols,
        )
        assertEquals(
            expectedTeamResultsProtocol.scores,
            teamResultsProtocol.scores
        )
    }

    @Test
    fun `Empty test`() {
        val teamResultsProtocol = SampleTeamResultsCalculator.calculate(
            groupResultProtocols = listOf(),
        )
        assertEquals(listOf(), teamResultsProtocol.scores)
    }

    @Test
    fun `Test division by zero`() {
        val group1 = AgeGroup("group1", OrderedCheckpointsRoute("", mutableListOf()), -100, 100, 2021)
        val groupResultsProtocols = listOf(
            GroupResultProtocol(
                group = group1,
                entries = listOf(
                    ParticipantWithFinalResult(p0, FinalParticipantResult.Finished(Time(0))),
                    ParticipantWithFinalResult(p1, FinalParticipantResult.Finished(Time(0))),
                ),
            )
        )
        assertFails {
            val teamResultsProtocol = SampleTeamResultsCalculator.calculate(
                groupResultProtocols = groupResultsProtocols,
            )
            println(teamResultsProtocol)
        }
    }
}