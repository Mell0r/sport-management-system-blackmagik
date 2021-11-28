package ru.emkn.kotlin

import org.junit.Test
import ru.emkn.kotlin.sms.*
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

internal class StartProtocolsByApplicationsTest {
    private val testApplications = listOf(
        listOf(",,,,", "First,Man,1,SomeGroup,"),
        listOf("EmptyOrg,,,,"),
        listOf("ShortLineOrg,,,,", "Short,Line,2,Group"),
        listOf("CorrectOrg,,,,", "Man,First,1,A,SomeSportCategory", "Man,Second,5,B,"),
        listOf("AnotherCorrectOrg,,,,", "Last1,Name1,4,A,", "Last2,Name2,5,B,F", "Last3,Name3,3,C,")
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
    fun applicationReadFromFileContentTest() {
        assertFails{ Application.readFromFileContent(testApplications[0]) }
        assertFails{ Application.readFromFileContent(testApplications[1]) }
        assertFails{ Application.readFromFileContent(testApplications[2]) }
    }

    @Test
    fun checkApplicantTest() {
        var aplt = testApplications[3][1].split(',')
        var commandName = "CorrectOrg"
        assertEquals(checkApplicant(
            Participant(0, testCompetition.year - aplt[2].toInt(), aplt[1], aplt[0], aplt[3], commandName, aplt[4]),
            testCompetition), false)

        aplt = testApplications[4][3].split(',')
        commandName = "AnotherCorrectOrg"
        assertEquals(checkApplicant(
            Participant(0, testCompetition.year - aplt[2].toInt(), aplt[1], aplt[0], aplt[3], commandName, aplt[4]),
            testCompetition), false)

        aplt = testApplications[3][2].split(',')
        commandName = "CorrectOrg"
        assertEquals(checkApplicant(
            Participant(0, testCompetition.year - aplt[2].toInt(), aplt[1], aplt[0], aplt[3], commandName, aplt[4]),
            testCompetition), true)
    }

    @Test
    fun getParticipantsListFromApplicationsTest() {
        val applications = listOf(Application.readFromFileContent(testApplications[3]),
            Application.readFromFileContent(testApplications[4]))
        applications[0].applicantsList.forEach {
            println(it)
        }
        println()
        getParticipantsListFromApplications(applications, testCompetition).list.forEach {
            println(it)
        }
        assertTrue { getParticipantsListFromApplications(applications, testCompetition) == testParticipantsList }
    }
}