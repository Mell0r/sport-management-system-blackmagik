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
    private val testRoutes = listOf(
        OrderedCheckpointsRoute("R", mutableListOf())
    )
    private val testGroups = listOf(
        AgeGroup("A", testRoutes[0], 1, 2),
        AgeGroup("B", testRoutes[0], 0, 10),
    )
    private val testCompetition = Competition(
        "", "", 6, "",
        testGroups,
        testRoutes,
    )
    private fun getTestGroupByLabel(label: String) = testCompetition.getGroupByLabelOrNull(label)!!
    private val testParticipantsList = ParticipantsList(
        listOf(
            Participant(1, 1, "Second", "Man", getTestGroupByLabel("B"), "CorrectOrg", ""),
            Participant(2, 2, "Name1", "Last1", getTestGroupByLabel("A"), "AnotherCorrectOrg", ""),
            Participant(3, 1, "Name2", "Last2", getTestGroupByLabel("B"), "AnotherCorrectOrg", "F")
        )
    )

    @Test
    fun applicationReadFromFileContentTest() {
        assertFails { Application.readFromFileContentAndCompetition(testApplications[0], testCompetition) }
        assertFails { Application.readFromFileContentAndCompetition(testApplications[1], testCompetition) }
        assertFails { Application.readFromFileContentAndCompetition(testApplications[2], testCompetition) }
    }

    @Test
    fun checkApplicantTest() {
        val aplt = testApplications[3][1].split(',')
        val commandName = "CorrectOrg"
        assertEquals(
            checkApplicant(
                Participant(
                    0,
                    testCompetition.year - aplt[3].toInt(),
                    aplt[2],
                    aplt[1],
                    getTestGroupByLabel(aplt[0]),
                    commandName,
                    aplt[4]
                ),
            ), false
        )
    }

    @Test
    fun getParticipantsListFromApplicationsTest() {
        val applications = listOf(
            Application.readFromFileContentAndCompetition(testApplications[3], testCompetition),
            Application.readFromFileContentAndCompetition(testApplications[4], testCompetition),
        )
        assertTrue {
            getParticipantsListFromApplications(
                applications,
                testCompetition
            ) == testParticipantsList
        }
    }
}