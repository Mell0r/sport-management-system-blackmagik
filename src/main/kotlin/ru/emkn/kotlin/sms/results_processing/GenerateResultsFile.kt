package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.GroupLabelT
import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.logErrorAndThrow
import kotlin.math.max

/**
 *
 */

data class ParticipantResult(
    val participantId: Int,
    val routeCompletionTime: Int?
)

/**
 * @return a map (that is, a list of pairs): a group label to a list of
 * strings - file content with the result of the respective group.
 */
fun generateFullResultsFile(
    results: Map<Int, Int?>,
    idToParticipantMapping: (Int) -> Participant
): Map<GroupLabelT, List<String>> {
    val idToTimePairs = results.entries.toList()
    val groupedByGroups = idToTimePairs.groupBy({ (id, _) ->
        idToParticipantMapping(id).supposedGroup
    }) { (id, completionTime) -> ParticipantResult(id, completionTime) }
    return groupedByGroups.mapValues { (_, participantResults) ->
        generateResultsWithinAGroup(participantResults, idToParticipantMapping)
    }
}

data class FieldInfo<T>(
    val fieldName: String,
    val generateFieldValue: (T) -> String
)

class PlayersPrinter<T>(
    private val fieldsInfo: List<FieldInfo<T>>
) {
    fun toTable(values: List<T>): List<String> {
        val headers = listOf(fieldsInfo.joinToString(",") { it.fieldName })
        val fieldValuesTable = values.map { value ->
            fieldsInfo.joinToString(",") {
                it.generateFieldValue(value)
            }
        }
        return headers + fieldValuesTable
    }
}

/*
It is very likely that in the future idToParticipantMapping will be needed
(for formatting purposes), thus I am suppressing the unused parameter warning.
 */
@Suppress("UNUSED_PARAMETER")
private fun generateResultsWithinAGroup(
    groupResults: List<ParticipantResult>,
    idToParticipantMapping: (Int) -> Participant
): List<String> {
    val bestResult = groupResults
        .mapNotNull { participantResult -> participantResult.routeCompletionTime }
        .maxOrNull() ?: logErrorAndThrow("the whole group is disqualified.")

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
        FieldInfo<Int>("Индивидуальный номер") { id -> id.toString() },
        FieldInfo("Результат") { id ->
            scoresSorted.first { it.id == id }.score?.toString() ?: "снят"
        }
    )).toTable(idsSorted)
}