package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.GroupLabelT
import ru.emkn.kotlin.sms.logErrorAndThrow
import kotlin.math.max

/**
 *
 */

data class ParticipantResult(
    val participantId: Int,
    val routeCompletionTime: Int?
)

fun generateFullResultsFile(
    results: Map<Int, Int?>,
    groupGetter: (Int) -> GroupLabelT
): Map<GroupLabelT, List<String>> {
    val idToTimePairs = results.entries.toList()
    val groupedByGroups = idToTimePairs.groupBy({ (id, _) ->
        groupGetter(id)
    }) { (id, completionTime) -> ParticipantResult(id, completionTime) }
    return groupedByGroups.mapValues { (groupLabel, participantResults) ->
        generateResultsWithinAGroup(participantResults)
    }
}

data class FieldInfo(
    val fieldName: String,
    val generateFieldValue: (Int) -> String
)

class PlayersPrinter(
    private val fieldsInfo: List<FieldInfo>
) {
    fun toTable(ids: List<Int>): List<String> {
        val headers = listOf(fieldsInfo.joinToString(",") { it.fieldName })
        val fieldValuesTable = ids.map { id ->
            fieldsInfo.joinToString(",") {
                it.generateFieldValue(
                    id
                )
            }
        }
        return headers + fieldValuesTable
    }
}

private fun generateResultsWithinAGroup(groupResults: List<ParticipantResult>): List<String> {
    val bestResult = groupResults
        .mapNotNull { participantResult -> participantResult.routeCompletionTime }
        .maxOrNull() ?: logErrorAndThrow("all group is disqualified.")

    class ParticipantScore(val id: Int, val score: Int?)

    val scores = groupResults.map { (id, time) ->
        ParticipantScore(id, time?.let { timeNotNull ->
            max(
                0,
                100 * (2 - timeNotNull / bestResult)
            )
        })
    }
    val scoresSorted = scores.filter { it.score != null }
        .sortedBy { it.score!! } + scores.filter { it.score == null }
    val idsSorted = scoresSorted.map { participantScore -> participantScore.id }
    return PlayersPrinter(listOf(
        FieldInfo("Индивидуальный номер") { id -> id.toString() },
        FieldInfo("Результат") { id ->
            scoresSorted.first { it.id == id }.score?.toString() ?: "снят"
        }
    )).toTable(idsSorted)
}