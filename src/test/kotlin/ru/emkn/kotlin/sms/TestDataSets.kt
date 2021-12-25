package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.results_processing.*
import ru.emkn.kotlin.sms.time.Time
import ru.emkn.kotlin.sms.time.s

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

/**
 * Used for:
 * 1. TimestampsProtocolProcessor tests
 * 2. LiveGroupResultProtocolGenerator tests
 * 3. GroupResultProtocolGenerator tests
 */
object TestDataSet2 {
    const val competitionYear = 0
    val mainRoute = OrderedCheckpointsRoute("main", mutableListOf("1", "2", "3"))
    val m10 = AgeGroup("М10", mainRoute, -100, 100, competitionYear)
    val f10 = AgeGroup("Ж10", mainRoute, -100, 100, competitionYear)
    val p1 = Participant(1, 10, "Иван", "Иванов", m10, "T1", "", Time(0))
    val p2 = Participant(2, 10, "Иван", "Неиванов", m10, "T2", "", Time(0))
    val p3 = Participant(3, 10, "Иван", "Дурак", m10, "T2", "", Time(0))
    val p4 = Participant(4, 10, "Афродита", "Иванова", f10, "T1", "", Time(0))
    val participantsList = ParticipantsList(listOf(p1, p2, p3, p4))

    val participantTimestampsProtocols = listOf(
        ParticipantTimestampsProtocol(
            id = p1.id,
            checkpointTimes = listOf(
                CheckpointAndTime("1", 5.s()),
                CheckpointAndTime("2", 10.s()),
                CheckpointAndTime("3", 15.s()),
            )
        ),
        ParticipantTimestampsProtocol(
            id = p2.id,
            checkpointTimes = listOf(
                CheckpointAndTime("1", 6.s()),
                CheckpointAndTime("2", 18.s()),
                CheckpointAndTime("3", 21.s()),
            )
        ),
        ParticipantTimestampsProtocol(
            id = p3.id,
            checkpointTimes = listOf(
                CheckpointAndTime("1", 5.s()),
                CheckpointAndTime("3", 10.s()),
                CheckpointAndTime("2", 20.s()),
            )
        ),
        ParticipantTimestampsProtocol(
            id = p4.id,
            checkpointTimes = listOf(
                CheckpointAndTime("1", 5.s()),
                CheckpointAndTime("2", 25.s()),
            )
        ),
    )
    val checkpointTimestampsProtocols = listOf(
        CheckpointTimestampsProtocol(
            checkpointLabel = "1",
            participantTimes = listOf(
                IdAndTime(p1.id, 5.s()),
                IdAndTime(p2.id, 6.s()),
                IdAndTime(p3.id, 5.s()),
                IdAndTime(p4.id, 5.s()),
            ),
        ),
        CheckpointTimestampsProtocol(
            checkpointLabel = "2",
            participantTimes = listOf(
                IdAndTime(p1.id, 10.s()),
                IdAndTime(p2.id, 18.s()),
                IdAndTime(p3.id, 20.s()),
                IdAndTime(p4.id, 25.s()),
            ),
        ),
        CheckpointTimestampsProtocol(
            checkpointLabel = "3",
            participantTimes = listOf(
                IdAndTime(p1.id, 15.s()),
                IdAndTime(p2.id, 21.s()),
                IdAndTime(p3.id, 10.s()),
            ),
        ),
    )
    val timestamps = listOf(
        ParticipantCheckpointTime(p1, "1", 5.s()),
        ParticipantCheckpointTime(p1, "2", 10.s()),
        ParticipantCheckpointTime(p1, "3", 15.s()),
        ParticipantCheckpointTime(p2, "1", 6.s()),
        ParticipantCheckpointTime(p2, "2", 18.s()),
        ParticipantCheckpointTime(p2, "3", 21.s()),
        ParticipantCheckpointTime(p3, "1", 5.s()),
        ParticipantCheckpointTime(p3, "3", 10.s()),
        ParticipantCheckpointTime(p3, "2", 20.s()),
        ParticipantCheckpointTime(p4, "1", 5.s()),
        ParticipantCheckpointTime(p4, "2", 25.s()),
    ).toSet()
    val liveGroupResultProtocols = listOf(
        LiveGroupResultProtocol(
            group = m10,
            entries = listOf(
                ParticipantWithLiveResult(p1, LiveParticipantResult.Finished(15.s())),
                ParticipantWithLiveResult(p2, LiveParticipantResult.Finished(21.s())),
                ParticipantWithLiveResult(p3, LiveParticipantResult.Disqualified()),
            ),
        ),
        LiveGroupResultProtocol(
            group = f10,
            entries = listOf(
                ParticipantWithLiveResult(p4, LiveParticipantResult.InProcess(2, 25.s()))
            ),
        ),
    )
    val groupResultProtocols = listOf(
        GroupResultProtocol(
            group = m10,
            entries = listOf(
                ParticipantWithFinalResult(p1, FinalParticipantResult.Finished(15.s())),
                ParticipantWithFinalResult(p2, FinalParticipantResult.Finished(21.s())),
                ParticipantWithFinalResult(p3, FinalParticipantResult.Disqualified()),
            ),
        ),
        GroupResultProtocol(
            group = f10,
            entries = listOf(
                ParticipantWithFinalResult(p4, FinalParticipantResult.Disqualified())
            ),
        ),
    )


    data class ByParticipantTestSet(
        val protocols: List<ParticipantTimestampsProtocol>,
        val expectedTimestamps: Set<ParticipantCheckpointTime>,
    )
    val byParticipantsTestSets = listOf(
        ByParticipantTestSet(
            participantTimestampsProtocols,
            timestamps,
        )
    )

    data class ByCheckpointTestSet(
        val protocols: List<CheckpointTimestampsProtocol>,
        val expectedTimestamps: Set<ParticipantCheckpointTime>,
    )
    val byCheckpointTestSets = listOf(
        ByCheckpointTestSet(
            checkpointTimestampsProtocols,
            timestamps,
        )
    )

    data class LiveGroupResultProtocolsTestSet(
        val timestamps: List<ParticipantCheckpointTime>,
        val expectedProtocols: Set<LiveGroupResultProtocol>,
    )
    val liveGroupResultProtocolsTestSets = listOf(
        LiveGroupResultProtocolsTestSet(
            timestamps.toList(),
            liveGroupResultProtocols.toSet(),
        )
    )

    data class GroupResultProtocolsTestSet(
        val timestamps: List<ParticipantCheckpointTime>,
        val expectedProtocols: Set<GroupResultProtocol>,
    )
    val groupResultProtocolsTestSets = listOf(
        GroupResultProtocolsTestSet(
            timestamps.toList(),
            groupResultProtocols.toSet(),
        )
    )

}