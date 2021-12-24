package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.csv.CsvDumpable
import ru.emkn.kotlin.sms.io.FileContent

class TeamResultsProtocol(scores: List<TeamToScore>) : CsvDumpable {
    val scores = scores.sortedBy { it.team }.sortedByDescending { it.score }
    private val places = generatePlaces()

    private fun generatePlaces(): List<Int> {
        val places = (1..scores.size).toMutableList()
        (0 until scores.lastIndex)
            .filter { scores[it].score == scores[it + 1].score }
            .forEach { places[it + 1] = places[it] }
        return places
    }

    override fun dumpToCsv(): FileContent {
        val header = listOf("Место,Команда,Баллы")
        val rest =
            scores.zip(places) { (team, score), place -> "$place,$team,$score" }
        return header + rest
    }

    override fun defaultCsvFileName() = "results-teams.csv"
}