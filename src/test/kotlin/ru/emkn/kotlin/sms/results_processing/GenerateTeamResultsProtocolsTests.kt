package ru.emkn.kotlin.sms.results_processing

import kotlin.test.*
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.time.Time

internal class GenerateTeamResultsProtocolsTests {

    val testCompetition = Competition(
        discipline = "Marathon",
        name = "SPb Autumn Marathon 2030",
        year = 2030,
        date = "10.10",
        groups = listOf("M12-15", "M16-18", "M19-35"),
        routes = listOf(
            Route("adults", listOf("Checkpoint1", "Checkpoint2", "Checkpoint3", "Checkpoint4", "Checkpoint5")),
            Route("kids", listOf("Checkpoint1", "Checkpoint2")),
        ),
        groupToRouteMapping = mapOf(
            "M12-15" to Route("kids", listOf("Checkpoint1", "Checkpoint2")),
            "M16-18" to Route("adults", listOf("Checkpoint1", "Checkpoint2", "Checkpoint3", "Checkpoint4", "Checkpoint5")),
            "M19-35" to Route("adults", listOf("Checkpoint1", "Checkpoint2", "Checkpoint3", "Checkpoint4", "Checkpoint5")),
        ),
        requirementByGroup = mapOf(
            "M12-15" to GroupRequirement(12, 15),
            "M16-18" to GroupRequirement(16, 18),
            "M19-35" to GroupRequirement(19, 35),
        ),
    )

    val testParticipantsList = ParticipantsList(listOf(
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

    val testGroupResultProtocols = listOf(
        GroupResultProtocol(
            groupName = "M12-15",
            entries = listOf(
                ParticipantAndTime(testParticipantsList.list[7], Time(500)),
                ParticipantAndTime(testParticipantsList.list[1], Time(600)),
                ParticipantAndTime(testParticipantsList.list[0], Time(600)),
                ParticipantAndTime(testParticipantsList.list[8], null),
            )
        ),
        GroupResultProtocol(
            groupName = "M16-18",
            entries = listOf(
                ParticipantAndTime(testParticipantsList.list[4], Time(1500)),
                ParticipantAndTime(testParticipantsList.list[9], Time(2000)),
                ParticipantAndTime(testParticipantsList.list[3], Time(3000)),
            )
        ),
        GroupResultProtocol(
            groupName = "M19-35",
            entries = listOf( // whole group was disqualified --- definitely a real situation
                ParticipantAndTime(testParticipantsList.list[2], null),
                ParticipantAndTime(testParticipantsList.list[5], null),
                ParticipantAndTime(testParticipantsList.list[6], null),
            )
        ),
    )

    val expectedTeamResultsProtocol = TeamResultsProtocol(listOf(
        TeamToScore("Team1", 260),
        TeamToScore("Team2", 100),
        TeamToScore("Team3", 67),
    ))

    @Test
    fun `Generate team results protocols test`() {
        val teamResultsProtocol = generateTeamResultsProtocol(
            groupResultProtocols = testGroupResultProtocols,
            participantsList = testParticipantsList,
            competitionConfig = testCompetition,
        )
        assertEquals(expectedTeamResultsProtocol.scores, teamResultsProtocol.scores)
    }

    @Test
    fun `Empty test`() {
        val teamResultsProtocol = generateTeamResultsProtocol(
            groupResultProtocols = listOf(),
            participantsList = ParticipantsList(listOf()),
            competitionConfig = testCompetition,
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
                    ParticipantAndTime(participantsList.list[0], Time(0)),
                    ParticipantAndTime(participantsList.list[1], Time(0)),
                ),
            )
        )
        assertFails {
            val teamResultsProtocol = generateTeamResultsProtocol(
                groupResultProtocols = groupResultsProtocols,
                participantsList = participantsList,
                competitionConfig = testCompetition,
            )
            println(teamResultsProtocol)
        }
    }
}