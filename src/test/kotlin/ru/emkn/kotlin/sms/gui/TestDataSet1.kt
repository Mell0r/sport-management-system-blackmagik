package ru.emkn.kotlin.sms.gui

import ru.emkn.kotlin.sms.AgeGroup
import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.OrderedCheckpointsRoute
import ru.emkn.kotlin.sms.gui.programState.FormingStartingProtocolsProgramState

object TestDataSet1 {
    // corresponds to test-data/GUI-tests-1
    // please DO NOT CHANGE!!!
    private val route1 =
        OrderedCheckpointsRoute("route1", mutableListOf("1", "2", "3"))
    private val gr1 = AgeGroup("gr1", route1, 1, 100)
    private val gr2 = AgeGroup("gr2", route1, 10, 15)
    private val competition = Competition(
        discipline = "Test Discipline",
        name = "Test Competition",
        year = 2021,
        date = "31.12",
        groups = listOf(gr1, gr2),
        routes = listOf(route1),
    )
    val formingStartingProtocolsProgramState =
        FormingStartingProtocolsProgramState(competition)
}