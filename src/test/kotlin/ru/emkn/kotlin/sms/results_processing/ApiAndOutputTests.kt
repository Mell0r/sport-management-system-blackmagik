package ru.emkn.kotlin.sms.results_processing

import org.junit.Test
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.time.Time
import kotlin.test.assertEquals

private fun Int.s() = Time(this)

internal class ApiTests {
    private val mainRoute =
        OrderedCheckpointsRoute("main", listOf("1", "2", "3"))
    private val shortRoute = OrderedCheckpointsRoute("short", listOf("1"))
    private val competition = Competition(
        "",
        "",
        0,
        "",
        listOf(AgeGroup("М10", mainRoute, -100, 100), AgeGroup("Ж10", mainRoute, -100, 100)),
        listOf(mainRoute, shortRoute),
    )

    private val participants = ParticipantsList(
        listOf(
            Participant(1, 10, "Иван", "Иванов", competition.getGroupByLabelOrNull("М10")!!, "T1", ""),
            Participant(2, 10, "Иван", "Неиванов", competition.getGroupByLabelOrNull("М10")!!, "T2", ""),
            Participant(3, 10, "Иван", "Дурак", competition.getGroupByLabelOrNull("М10")!!, "T2", ""),
            Participant(4, 10, "Афродита", "Иванова", competition.getGroupByLabelOrNull("Ж10")!!, "T1", ""),
        )
    )
    private val startingProtocols = listOf(
        StartingProtocol(
            competition.getGroupByLabelOrNull("М10")!!,
            listOf(
                StartingProtocolEntry(1, 0.s()),
                StartingProtocolEntry(2, 0.s()),
                StartingProtocolEntry(3, 0.s())
            )
        ), StartingProtocol(
            competition.getGroupByLabelOrNull("Ж10")!!,
            listOf(
                StartingProtocolEntry(4, 0.s()),
            )
        )
    )


    private fun quickProtocolEntryList(
        firstTime: Int,
        secondTime: Int,
        thirdTime: Int
    ) =
        listOf(
            CheckpointLabelAndTime("1", firstTime.s()),
            CheckpointLabelAndTime("2", secondTime.s()),
            CheckpointLabelAndTime("3", thirdTime.s()),
        )


    @Test
    fun properResultGenerationForParticipantTimestampsProtocols() {
        val resultProtocols = sampleResultProtocols()
        assertEquals(2, resultProtocols.size)
        val maleResults = resultProtocols.single { it.group.label == "М10" }
        assertEquals(
            listOf(1, 3, 2),
            maleResults.entries.map { it.id })
        assertEquals(
            listOf(30.s(), 90.s(), null),
            maleResults.entries.map { it.totalTime })
        val femaleResults = resultProtocols.single { it.group.label == "Ж10" }
        assertEquals(4, femaleResults.entries.single().id)
        assertEquals(20, femaleResults.entries.single().totalTime?.asSeconds())
    }

    private fun sampleResultProtocols(): List<GroupResultProtocol> {
        val protocols = listOf(
            ParticipantTimestampsProtocol(
                1,
                quickProtocolEntryList(10, 20, 30)
            ),
            ParticipantTimestampsProtocol(
                2,
                quickProtocolEntryList(20, 10, 60)
            ),
            ParticipantTimestampsProtocol(
                3,
                quickProtocolEntryList(10, 40, 90)
            ), ParticipantTimestampsProtocol(
                4,
                quickProtocolEntryList(5, 15, 20)
            )
        )
        return generateResultsProtocolsOfParticipant(
            participants,
            startingProtocols,
            protocols,
            competition
        )
    }

    //       id
//        1: 1 4 6
//        2: 1 2 3
//        3: 5 2 4
//
    @Test
    fun properResultGenerationForCheckpointTimestampsProtocols() {
        val protocols = listOf(
            CheckpointTimestampsProtocol(
                "1",
                listOf(
                    IdAndTime(1, 1.s()),
                    IdAndTime(2, 1.s()),
                    IdAndTime(3, 5.s())
                )
            ), CheckpointTimestampsProtocol(
                "2",
                listOf(
                    IdAndTime(1, 4.s()),
                    IdAndTime(2, 2.s()),
                    IdAndTime(3, 2.s())
                )
            ), CheckpointTimestampsProtocol(
                "3",
                listOf(
                    IdAndTime(1, 6.s()),
                    IdAndTime(2, 3.s()),
                    IdAndTime(3, 4.s())
                )
            )
        )
        val maleParticipants = ParticipantsList(participants.list.dropLast(1))
        val resultProtocols = generateResultsProtocolsOfCheckpoint(
            maleParticipants,
            startingProtocols,
            protocols,
            competition
        )
        assertEquals(1, resultProtocols.size)
        val maleResults = resultProtocols.single { it.group.label == "М10" }
        assertEquals(
            listOf(2, 1, 3),
            maleResults.entries.map { it.id })
        assertEquals(
            listOf(3.s(), 6.s(), null),
            maleResults.entries.map { it.totalTime })
    }

    @Test
    fun testOutput() {
        val sampleResultProtocols = sampleResultProtocols()
        val maleProtocol =
            sampleResultProtocols.single { it.group.label == "М10" }
        assertEquals(
            listOf(
                "М10",
                "Место,Индивидуальный номер,Результат",
                "1,1,00:00:30",
                "2,3,00:01:30",
                "3,2,снят"
            ).asLines(), maleProtocol.dumpToCsv().asLines()
        )
    }

    @Test
    fun peopleWithSameResultHaveSamePlaces() {
        val group = AgeGroup("М10", shortRoute, -100, 100)
        val participantsShort = ParticipantsList(
            listOf(
                Participant(1, 10, "Иван", "А", group, "T1", ""),
                Participant(2, 10, "Иван", "Б", group, "T2", ""),
                Participant(3, 10, "Иван", "В", group, "T2", ""),
                Participant(4, 10, "Иван", "Г", group, "T1", ""),
                Participant(5, 10, "Иван", "Д", group, "T1", ""),
                Participant(6, 10, "Иван", "Е", group, "T1", ""),
            )
        )
        val protocolsWithSameTime = listOf(
            CheckpointTimestampsProtocol(
                "1",
                listOf(
                    IdAndTime(1, 10.s()),
                    IdAndTime(2, 10.s()),
                    IdAndTime(3, 20.s()),
                    IdAndTime(4, 20.s()),
                    IdAndTime(5, 20.s()),
                    IdAndTime(6, 30.s())
                )
            )
        )
        val startingProtocolsShort = listOf(
            StartingProtocol(
                group,
                listOf(
                    StartingProtocolEntry(1, 0.s()),
                    StartingProtocolEntry(2, 0.s()),
                    StartingProtocolEntry(3, 0.s()),
                    StartingProtocolEntry(4, 0.s()),
                    StartingProtocolEntry(5, 0.s()),
                    StartingProtocolEntry(6, 0.s())
                )
            )
        )
        val shortCompetition = Competition(
            "",
            "",
            0,
            "",
            listOf(),
            listOf(shortRoute),
        )
        val groupResultProtocol =
            generateResultsProtocolsOfCheckpoint(
                participantsShort,
                startingProtocolsShort,
                protocolsWithSameTime,
                shortCompetition
            ).single { it.group.label == "М10" }
        assertEquals(
            groupResultProtocol.dumpToCsv().asLines(), """
            М10
            Место,Индивидуальный номер,Результат
            1,1,00:00:10
            1,2,00:00:10
            3,3,00:00:20
            3,4,00:00:20
            3,5,00:00:20
            6,6,00:00:30
        """.trimIndent()
        )
    }

    @Test
    fun testDisqualificationOnFalseStart() {
        val participants = ParticipantsList(
            listOf(Participant(1, 10, "Иван", "Иванов", competition.getGroupByLabelOrNull("М10")!!, "T1", ""))
        )
        val startingProtocols = listOf(
            StartingProtocol(competition.getGroupByLabelOrNull("М10")!!, listOf(StartingProtocolEntry(1, 100.s())))
        )
        val route = OrderedCheckpointsRoute("main", listOf("1", "2"))
        val competition = Competition(
            "",
            "",
            0,
            "",
            listOf(competition.getGroupByLabelOrNull("М10")!!),
            listOf(route),
        )
        val checkpointProtocols = listOf(
            CheckpointTimestampsProtocol("1", listOf(IdAndTime(1, 10.s()))),
            CheckpointTimestampsProtocol("2", listOf(IdAndTime(1, 110.s()))),
        )
        val results = generateResultsProtocolsOfCheckpoint(
            participants,
            startingProtocols,
            checkpointProtocols,
            competition
        )
        assertEquals(null, results.single().entries.single().totalTime)
    }
}

private fun List<String>.asLines(): String {
    return joinToString("\n")
}
