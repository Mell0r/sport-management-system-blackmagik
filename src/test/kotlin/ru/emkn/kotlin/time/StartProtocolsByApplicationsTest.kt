package ru.emkn.kotlin.time

import org.junit.Test
import ru.emkn.kotlin.sms.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class StartProtocolsByApplicationsTest {
    private val testApplications = listOf(
        listOf(
            listOf("", "", "", "", ""),
            listOf("First", "Man", "1", "SomeGroup", "")
        ),
        listOf(listOf("EmptyOrg", "", "", "", "")),
        listOf(
            listOf("ShortLineOrg", "", "", "", ""),
            listOf("Short", "Line", "2", "Group")
        ),
        listOf(
            listOf("CorrectOrg", "", "", "", ""),
            listOf("Man", "First", "1", "A", "SomeSportCategory"),
            listOf("Man", "Second", "5", "B", "")
        ),
        listOf(
            listOf("AnotherCorrectOrg", "", "", "", ""),
            listOf("Last1", "Name1", "4", "A", ""),
            listOf("Last2", "Name2", "5", "B", "F"),
            listOf("Last3", "Name3", "3", "C", "")
        )
    )
    val testParticipantsList = ParticipantsList(listOf(
            Participant(1, 1, "Second", "Man", "B", "CorrectOrg", ""),
            Participant(2, 2, "Name1", "Last1", "A", "AnotherCorrectOrg", ""),
            Participant(3, 1, "Name2", "Last2", "B", "AnotherCorrectOrg", "F")
    ))
    private val testCompetition = Competition("", "", 6, "", listOf("A", "B"),
        listOf(Route("R", listOf())),
        mapOf("A" to Route("R", listOf()), "B" to Route("R", listOf())),
        mapOf("A" to GroupRequirement(1, 2), "B" to GroupRequirement(0, 10)))

    @Test
    fun checkApplicationFormatTest() {
        assertEquals(checkApplicationFormat(testApplications[0], 0), false)
        assertEquals(checkApplicationFormat(testApplications[1], 1), false)
        assertEquals(checkApplicationFormat(testApplications[2], 2), false)
        assertEquals(checkApplicationFormat(testApplications[3], 3), true)
        assertEquals(checkApplicationFormat(testApplications[4], 4), true)
    }

    @Test
    fun checkApplicantTest() {
        var aplt = testApplications[3][1]
        var commandName = "CorrectOrg"
        assertEquals(checkApplicant(
            Participant(0, testCompetition.year - aplt[2].toInt(), aplt[1], aplt[0], aplt[3], commandName, aplt[4]),
            testCompetition), false)

        aplt = testApplications[4][3]
        commandName = "AnotherCorrectOrg"
        assertEquals(checkApplicant(
            Participant(0, testCompetition.year - aplt[2].toInt(), aplt[1], aplt[0], aplt[3], commandName, aplt[4]),
            testCompetition), false)

        aplt = testApplications[3][2]
        commandName = "CorrectOrg"
        assertEquals(checkApplicant(
            Participant(0, testCompetition.year - aplt[2].toInt(), aplt[1], aplt[0], aplt[3], commandName, aplt[4]),
            testCompetition), true)
    }

    @Test
    fun getParticipantsListFromApplicationsTest() {
        val applications = testApplications.map { it.map { line -> line.joinToString(",") } }
        assertTrue { getParticipantsListFromApplications(applications, testCompetition) == testParticipantsList }
    }
}