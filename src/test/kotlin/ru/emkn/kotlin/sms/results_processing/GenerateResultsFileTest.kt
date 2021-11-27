package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.Participant
import kotlin.test.Test
import kotlin.test.assertEquals

internal class GenerateResultsFileTest {
    private val sampleParticipants = mapOf(
        1 to Participant(1, 10, "Иван", "Иванов", "М10", "T1", ""),
        2 to Participant(2, 10, "Иван", "Неиванов", "М10", "T1", ""),
        3 to Participant(3, 10, "Иван", "Дурак", "М10", "T1", ""),
        4 to Participant(4, 10, "Афродита", "Иванова", "Ж10", "T1", ""),
        5 to Participant(5, 10, "Эльвира", "Козлова", "Ж10", "T1", ""),
        6 to Participant(6, 10, "Эльвира", "Андреева", "Ж10", "T1", ""),
    )
    private val idToParticipantMapping = { id: Int -> sampleParticipants[id]!! }

    private fun List<String>.asLines() = joinToString("\n")

    @Test
    fun testGenerateFullResultsFile() {
        val results = mapOf(
            1 to 10,
            2 to 20,
            3 to null,
            4 to 10,
            5 to 20,
            6 to 20
        )
        val resultFiles =
            generateFullResultsFile(results, idToParticipantMapping)
        assertEquals(2, resultFiles.size)
        val resultsForMales = resultFiles["М10"]!!
        assertEquals(
            listOf(
                "М10",
                "Место,Индивидуальный номер,Результат",
                "1,1,00:00:10",
                "2,2,00:00:20",
                "3,3,снят",
            ).asLines(), resultsForMales.asLines()
        )
        val resultsForFemales = resultFiles["Ж10"]!!
        assertEquals(
            listOf(
                "Ж10",
                "Место,Индивидуальный номер,Результат",
                "1,4,00:00:10",
                "2,6,00:00:20", // 6 must be before 5 due to her surname being less lexicographically
                "3,5,00:00:20",
            ).asLines(), resultsForFemales.asLines()
        )
    }
}