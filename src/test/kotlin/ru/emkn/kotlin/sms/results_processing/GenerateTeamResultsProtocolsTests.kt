package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.time.Time
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

internal class GenerateTeamResultsProtocolsTests {

    private val testParticipantsList = ParticipantsList(listOf(
        Participant(0, 14, "Name2", "Surname2", "M12-15", "Team1", ""),
        Participant(1, 13, "Name1", "Surname1", "M12-15", "Team1", ""),
        Participant(2, 30, "Name2", "Surname2", "M19-35", "Team1", ""),
        Participant(3, 18, "Name3", "Surname3", "M16-18", "Team1", ""),
        Participant(4, 16, "Name4", "Surname4", "M16-18", "Team1", ""),

        Participant(5, 21, "Name5", "Surname5", "M19-35", "Team2", ""),
        Participant(6, 34, "Name6", "Surname6", "M19-35", "Team2", ""),
        Participant(7, 12, "Name7", "Surname7", "M12-15", "Team2", ""),

        Participant(8, 15, "Name8", "Surname8", "M12-15", "Team3", ""),
        Participant(9, 16, "Name9", "Surname9", "M16-18", "Team3", ""),
    ))

    private val testGroupResultProtocols = listOf(
        GroupResultProtocol(
            groupName = "M12-15",
            entries = listOf(
                ParticipantAndTime(7, Time(500)),
                ParticipantAndTime(1, Time(600)),
                ParticipantAndTime(0, Time(600)),
                ParticipantAndTime(8, null),
            )
        ),
        GroupResultProtocol(
            groupName = "M16-18",
            entries = listOf(
                ParticipantAndTime(4, Time(1500)),
                ParticipantAndTime(9, Time(2000)),
                ParticipantAndTime(3, Time(3000)),
            )
        ),
        GroupResultProtocol(
            groupName = "M19-35",
            entries = listOf(
                // whole group was disqualified --- definitely a real situation
                ParticipantAndTime(2, null),
                ParticipantAndTime(5, null),
                ParticipantAndTime(6, null),
            )
        ),
    )

    private val expectedTeamResultsProtocol = TeamResultsProtocol(listOf(
        TeamToScore("Team1", 260),
        TeamToScore("Team2", 100),
        TeamToScore("Team3", 67),
    ))

    @Test
    fun `Generate team results protocols test`() {
        val teamResultsProtocol = generateTeamResultsProtocol(
            groupResultProtocols = testGroupResultProtocols,
            participantsList = testParticipantsList
        )
        assertEquals(expectedTeamResultsProtocol.scores, teamResultsProtocol.scores)
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
        val participantsList = ParticipantsList(listOf(
            Participant(0, 18, "Name1", "Surname1", "group1", "Team1", ""),
            Participant(1, 19, "Name2", "Surname2", "group1", "Team2", ""),
        ))
        val groupResultsProtocols = listOf(
            GroupResultProtocol(
                groupName = "group1",
                entries = listOf(
                    ParticipantAndTime(0, Time(0)),
                    ParticipantAndTime(1, Time(0)),
                ),
            )
        )
        assertFails {
            val teamResultsProtocol = generateTeamResultsProtocol(
                groupResultProtocols = groupResultsProtocols,
                testParticipantsList
            )
            println(teamResultsProtocol)
        }
    }
}