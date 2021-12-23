package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.time.Time
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

internal class GenerateTeamResultsProtocolsTests {
    private val testRoute = OrderedCheckpointsRoute("", mutableListOf())
    private val groupM12To15 = AgeGroup("M12-15", testRoute, 12, 15, 2021)
    private val groupM19To35 = AgeGroup("M19-35", testRoute, 19, 35, 2021)
    private val groupM16To18 = AgeGroup("M16-18", testRoute, 16, 18, 2021)

    private val testParticipantsList = ParticipantsList(
        listOf(
            Participant(0, 14, "Name2", "Surname2", groupM12To15, "Team1", "", 0.s()),
            Participant(1, 13, "Name1", "Surname1", groupM12To15, "Team1", "", 0.s()),
            Participant(2, 30, "Name2", "Surname2", groupM19To35, "Team1", "", 0.s()),
            Participant(3, 18, "Name3", "Surname3", groupM16To18, "Team1", "", 0.s()),
            Participant(4, 16, "Name4", "Surname4", groupM16To18, "Team1", "", 0.s()),

            Participant(5, 21, "Name5", "Surname5", groupM19To35, "Team2", "", 0.s()),
            Participant(6, 34, "Name6", "Surname6", groupM19To35, "Team2", "", 0.s()),
            Participant(7, 12, "Name7", "Surname7", groupM12To15, "Team2", "", 0.s()),

            Participant(8, 15, "Name8", "Surname8", groupM12To15, "Team3", "", 0.s()),
            Participant(9, 16, "Name9", "Surname9", groupM16To18, "Team3", "", 0.s()),
        )
    )

    private val testGroupResultProtocols = listOf(
        GroupResultProtocol(
            group = groupM12To15,
            entries = listOf(
                IdWithFinalResult(7, FinalParticipantResult.Finished(Time(500))),
                IdWithFinalResult(1, FinalParticipantResult.Finished(Time(600))),
                IdWithFinalResult(0, FinalParticipantResult.Finished(Time(600))),
                IdWithFinalResult(8, FinalParticipantResult.Disqualified()),
            )
        ),
        GroupResultProtocol(
            group = groupM16To18,
            entries = listOf(
                IdWithFinalResult(4, FinalParticipantResult.Finished(Time(1500))),
                IdWithFinalResult(9, FinalParticipantResult.Finished(Time(2000))),
                IdWithFinalResult(3, FinalParticipantResult.Finished(Time(3000))),
            )
        ),
        GroupResultProtocol(
            group = groupM19To35,
            entries = listOf(
                // whole group was disqualified --- definitely a real situation
                IdWithFinalResult(2, FinalParticipantResult.Disqualified()),
                IdWithFinalResult(5, FinalParticipantResult.Disqualified()),
                IdWithFinalResult(6, FinalParticipantResult.Disqualified()),
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
        val teamResultsProtocol = generateTeamResultsProtocol(
            groupResultProtocols = testGroupResultProtocols,
            participantsList = testParticipantsList
        )
        assertEquals(
            expectedTeamResultsProtocol.scores,
            teamResultsProtocol.scores
        )
    }

    @Test
    fun `Empty test`() {
        val teamResultsProtocol = generateTeamResultsProtocol(
            groupResultProtocols = listOf(),
            testParticipantsList
        )
        assertEquals(listOf(), teamResultsProtocol.scores)
    }

    @Test
    fun `Test division by zero`() {
        val group1 = AgeGroup("group1", OrderedCheckpointsRoute("", mutableListOf()), -100, 100, 2021)
        val participantsList = ParticipantsList(
            listOf(
                Participant(0, 18, "Name1", "Surname1", group1, "Team1", "", 0.s()),
                Participant(1, 19, "Name2", "Surname2", group1, "Team2", "", 0.s()),
            )
        )
        val groupResultsProtocols = listOf(
            GroupResultProtocol(
                group = group1,
                entries = listOf(
                    IdWithFinalResult(0, FinalParticipantResult.Finished(Time(0))),
                    IdWithFinalResult(1, FinalParticipantResult.Finished(Time(0))),
                ),
            )
        )
        assertFails {
            val teamResultsProtocol = generateTeamResultsProtocol(
                groupResultProtocols = groupResultsProtocols,
                participantsList
            )
            println(teamResultsProtocol)
        }
    }
}