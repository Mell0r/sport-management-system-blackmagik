package ru.emkn.kotlin.sms.startcfg

import ru.emkn.kotlin.sms.AgeGroup
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.OrderedCheckpointsRoute
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class ApplicationProcessorTests {
    private val route = OrderedCheckpointsRoute("", mutableListOf())
    private val currentYear = 2021
    private val group = AgeGroup("group", route, 10, 20, currentYear)
    private val competition = Competition(
        "",
        "",
        0,
        "",
        listOf(group),
        listOf(route)
    )

    private fun applicant(groupLabel: String, num: Int, age: Int) =
        Applicant(groupLabel, "$num", "$num", currentYear - age, "", "")

    @Test
    fun `filtering by age restriction test`() {
        val applicants = listOf(
            applicant(groupLabel = "group", num = 0, age = 10),
            applicant(groupLabel = "noGroup", num = 1, age = 10),
            applicant(groupLabel = "group", num = 2, age = 14),
            applicant(groupLabel = "group", num = 3, age = 16),
            applicant(groupLabel = "noGroup", num = 4, age = 25),
            applicant(groupLabel = "group", num = 5, age = 25),
            applicant(groupLabel = "group", num = 6, age = 5),
        )
        val application = Application("team1", applicants)
        val processed =
            ApplicationProcessor(competition, mutableListOf(application))
                .process()
        assertEquals(setOf("0", "2", "3"), processed.map { it.name }.toSet())

    }

    @Test
    fun `applications with same team name test`() {
        val applications = mutableListOf(
            Application("team1", listOf()),
            Application("team1", listOf())
        )
        assertFailsWith<IllegalArgumentException> {
            ApplicationProcessor(competition, applications).process()
        }
    }

    @Test
    fun `applications with different team name test`() {
        val applications = mutableListOf(
            Application("team1", listOf()),
            Application("team2", listOf()),
            Application("team3", listOf())
        )
        // should not throw
        ApplicationProcessor(competition, applications).process()
    }
}