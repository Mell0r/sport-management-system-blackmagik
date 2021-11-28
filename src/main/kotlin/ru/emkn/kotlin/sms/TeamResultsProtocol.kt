package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.results_processing.FileContent

data class TeamToScore(val team: String, val score: Int)
class TeamResultsProtocol(scores: List<TeamToScore>) : CsvDumpable {
    private val scores = scores.sortedBy { it.team }.sortedBy { it.score }
    private val places = generatePlaces()

    private fun generatePlaces(): List<Int> {
        val places = (1..scores.size).toMutableList()
        for (i in 0 until scores.lastIndex) {
            if (scores[i].score == scores[i + 1].score)
                places[i + 1] = places[i]
        }
        return places
    }

    override fun dumpToCsv(): FileContent {
        val header = listOf("Место,Команда,Баллы")
        val rest =
            scores.zip(places) { (team, score), place -> "$place,$team,$score" }
        return header + rest
    }
}