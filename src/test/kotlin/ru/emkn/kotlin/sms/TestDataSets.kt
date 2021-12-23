package ru.emkn.kotlin.sms

object TestDataSetCompetition1 {
    private val testRoutes = listOf(
        OrderedCheckpointsRoute(
            "orderedRoute1",
            mutableListOf("c1", "c2", "c3")
        ),
        OrderedCheckpointsRoute(
            "orderedRoute2",
            mutableListOf("c2", "c1", "c3", "c4", "c1", "c2")
        ),
        OrderedCheckpointsRoute(
            "orderedRoute3",
            mutableListOf("c6", "c7", "c1", "c2", "c5", "c1", "c2", "c1")
        ),
        OrderedCheckpointsRoute(
            "orderedRoute4",
            mutableListOf("c2", "c1", "c3")
        ),
        AtLeastKCheckpointsRoute(
            "ALKCRoute1",
            mutableSetOf("c1", "c2", "c3", "c5"),
            3
        ),
        AtLeastKCheckpointsRoute(
            "ALKCRoute2",
            mutableSetOf("c1", "c2", "c3", "c5"),
            2
        ),
        AtLeastKCheckpointsRoute(
            "ALKCRoute3",
            mutableSetOf("c5", "c6", "c7"),
            2
        ),
    )
    private const val competitionYear = 2021
    private val testGroups = listOf(
        AgeGroup("group0", testRoutes[0], 18, 21, competitionYear),
        AgeGroup("group1", testRoutes[1], 25, 35, competitionYear),
        AgeGroup("group2", testRoutes[2], 20, 50, competitionYear),
        AgeGroup("group3", testRoutes[3], 9, 14, competitionYear),
        AgeGroup("group4", testRoutes[4], 15, 17, competitionYear),
        AgeGroup("group5", testRoutes[5], 8, 14, competitionYear),
        AgeGroup("group6", testRoutes[6], 10, 23, competitionYear),
        AgeGroup("group0_1", testRoutes[0], 30, 40, competitionYear),
        AgeGroup("group0_2", testRoutes[0], 23, 36, competitionYear),
        AgeGroup("group1_1", testRoutes[1], 18, 28, competitionYear),
        AgeGroup("group4_1", testRoutes[4], 25, 41, competitionYear),
    )
    val testCompetitions = listOf(
        Competition(
            discipline = "discipline1",
            name = "competition1",
            year = competitionYear,
            date = "01.01",
            groups = testGroups,
            routes = testRoutes,
        ),
        Competition(
            discipline = "discipline2",
            name = "competition2",
            year = 2022,
            date = "02.02",
            groups = listOf(testGroups[1], testGroups[5], testGroups[2], testGroups[0], testGroups[10], testGroups[7]),
            routes = listOf(testRoutes[1], testRoutes[5], testRoutes[2], testRoutes[0], testRoutes[4], testRoutes[3]),
        ),
        Competition(
            discipline = "discipline3",
            name = "competition3",
            year = 2023,
            date = "03.03",
            groups = listOf(testGroups[3], testGroups[4], testGroups[10], testGroups[9], testGroups[7]),
            routes = listOf(testRoutes[3], testRoutes[1], testRoutes[4], testRoutes[0]),
        ),
        Competition(
            discipline = "",
            name = "",
            year = 0,
            date = "",
            groups = listOf(),
            routes = listOf(),
        ),
    )
}

object TestDataSetCompetition1WithoutAtLeastKRoutes {
    private val testRoutes = listOf(
        OrderedCheckpointsRoute(
            "orderedRoute1",
            mutableListOf("c1", "c2", "c3")
        ),
        OrderedCheckpointsRoute(
            "orderedRoute2",
            mutableListOf("c2", "c1", "c3", "c4", "c1", "c2")
        ),
        OrderedCheckpointsRoute(
            "orderedRoute3",
            mutableListOf("c6", "c7", "c1", "c2", "c5", "c1", "c2", "c1")
        ),
        OrderedCheckpointsRoute(
            "orderedRoute4",
            mutableListOf("c2", "c1", "c3")
        ),
    )
    private const val competitionYear = 2021
    private val testGroups = listOf(
        AgeGroup("group0", testRoutes[0], 18, 21, competitionYear),
        AgeGroup("group1", testRoutes[1], 25, 35, competitionYear),
        AgeGroup("group2", testRoutes[2], 20, 50, competitionYear),
        AgeGroup("group3", testRoutes[3], 9, 14, competitionYear),
        AgeGroup("group4", testRoutes[1], 15, 17, competitionYear),
        AgeGroup("group5", testRoutes[2], 8, 14, competitionYear),
        AgeGroup("group6", testRoutes[3], 10, 23, competitionYear),
        AgeGroup("group0_1", testRoutes[0], 30, 40, competitionYear),
        AgeGroup("group0_2", testRoutes[0], 23, 36, competitionYear),
        AgeGroup("group1_1", testRoutes[1], 18, 28, competitionYear),
        AgeGroup("group4_1", testRoutes[0], 25, 41, competitionYear),
    )
    val testCompetitions = listOf(
        Competition(
            discipline = "discipline1",
            name = "competition1",
            year = competitionYear,
            date = "01.01",
            groups = testGroups,
            routes = testRoutes,
        ),
        Competition(
            discipline = "discipline2",
            name = "competition2",
            year = competitionYear,
            date = "02.02",
            groups = listOf(testGroups[1], testGroups[5], testGroups[2], testGroups[0], testGroups[10], testGroups[7]),
            routes = listOf(testRoutes[1], testRoutes[2], testRoutes[0], testRoutes[3]),
        ),
        Competition(
            discipline = "discipline3",
            name = "competition3",
            year = competitionYear,
            date = "03.03",
            groups = listOf(testGroups[3], testGroups[4], testGroups[10], testGroups[9], testGroups[7]),
            routes = listOf(testRoutes[3], testRoutes[1], testRoutes[0]),
        ),
        Competition(
            discipline = "",
            name = "",
            year = 0,
            date = "",
            groups = listOf(),
            routes = listOf(),
        ),
    )
}
