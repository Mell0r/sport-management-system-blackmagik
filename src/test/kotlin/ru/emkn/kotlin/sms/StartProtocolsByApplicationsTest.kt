package ru.emkn.kotlin.sms

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

internal class StartProtocolsByApplicationsTest {
    private val testApplications = listOf(
        listOf(",,,,", "SomeGroup,First,Man,1,"),
        listOf("EmptyOrg,,,,"),
        listOf("ShortLineOrg,,,,", "Group,Short,Line,2"),
        listOf(
            "CorrectOrg,,,,",
            "A,Man,First,1,SomeSportCategory",
            "B,Man,Second,5,"
        ),
        listOf(
            "AnotherCorrectOrg,,,,",
            "A,Last1,Name1,4,",
            "B,Last2,Name2,5,F",
            "C,Last3,Name3,3,"
        )
    )
    private val testParticipantsList = ParticipantsList(
        listOf(
            Participant(1, 1, "Second", "Man", "B", "CorrectOrg", ""),
            Participant(2, 2, "Name1", "Last1", "A", "AnotherCorrectOrg", ""),
            Participant(3, 1, "Name2", "Last2", "B", "AnotherCorrectOrg", "F")
        )
    )
    private val testCompetition = Competition(
        "", "", 6, "", listOf("A", "B"),
        listOf(Route("R", listOf())),
        mapOf("A" to Route("R", listOf()), "B" to Route("R", listOf())),
        mapOf("A" to GroupRequirement(1, 2), "B" to GroupRequirement(0, 10))
    )

    @Test
    fun applicationReadFromFileContentTest() {
        assertFails { Application.readFromFileContent(testApplications[0]) }
        assertFails { Application.readFromFileContent(testApplications[1]) }
        assertFails { Application.readFromFileContent(testApplications[2]) }
    }

    @Test
    fun checkApplicantTest() {
        var aplt = testApplications[3][1].split(',')
        var commandName = "CorrectOrg"
        assertEquals(
            checkApplicant(
                Participant(
                    0,
                    testCompetition.year - aplt[3].toInt(),
                    aplt[2],
                    aplt[1],
                    aplt[0],
                    commandName,
                    aplt[4]
                ),
                testCompetition
            ), false
        )

        aplt = testApplications[4][3].split(',')
        commandName = "AnotherCorrectOrg"
        assertEquals(
            checkApplicant(
                Participant(
                    0,
                    testCompetition.year - aplt[3].toInt(),
                    aplt[2],
                    aplt[1],
                    aplt[0],
                    commandName,
                    aplt[4]
                ),
                testCompetition
            ), false
        )

        aplt = testApplications[3][2].split(',')
        commandName = "CorrectOrg"
        assertEquals(
            checkApplicant(
                Participant(
                    0,
                    testCompetition.year - aplt[3].toInt(),
                    aplt[2],
                    aplt[1],
                    aplt[0],
                    commandName,
                    aplt[4]
                ),
                testCompetition
            ), true
        )
    }

    @Test
    fun getParticipantsListFromApplicationsTest() {
        val applications = listOf(
            Application.readFromFileContent(testApplications[3]),
            Application.readFromFileContent(testApplications[4])
        )
        assertTrue {
            getParticipantsListFromApplications(
                applications,
                testCompetition
            ) == testParticipantsList
        }
    }
}