package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.GroupLabelT
import ru.emkn.kotlin.sms.Participant
import ru.emkn.kotlin.sms.time.Time

/**
 *
 */

data class ParticipantResult(
    val participantId: Int,
    val routeCompletionTime: Time?
)

/**
 * @return a map (that is, a list of pairs): a group label to a list of
 * strings - file content with the result of the respective group.
 */
fun generateFullResultsFile(
    results: Map<Int, Int?>,
    idToParticipantMapping: (Int) -> Participant
): Map<GroupLabelT, FileContent> {
    val idToTimePairs = results.entries.toList()
    val groupedByGroups = idToTimePairs.groupBy({ (id, _) ->
        idToParticipantMapping(id).supposedGroup
    }) { (id, completionTime) ->
        ParticipantResult(id,
            completionTime?.let { Time(it) })
    }
    return groupedByGroups.mapValues { (groupLabel, participantResults) ->
        generateResultsWithinAGroup(
            participantResults,
            idToParticipantMapping,
            groupLabel
        )
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
    idToParticipantMapping: (Int) -> Participant,
    groupLabel: GroupLabelT
): List<String> {
    val idsSorted =
        sortedGroupResultsForResultsTable(groupResults, idToParticipantMapping)
            .map { it.participantId }
    var placeCounter = 1
    return listOf(groupLabel) + PlayersPrinter(listOf(
        FieldInfo<Int>("Место") { placeCounter++.toString() },
        FieldInfo("Индивидуальный номер") { id: Int -> id.toString() },
        FieldInfo("Результат") { id ->
            groupResults.first { it.participantId == id }.routeCompletionTime?.toString()
                ?: "снят"
        }
    )).toTable(idsSorted)
}

private fun sortedGroupResultsForResultsTable(
    groupResults: List<ParticipantResult>,
    idToParticipantMapping: (Int) -> Participant
) =
    groupResults.filter { it.routeCompletionTime != null }
        .sortedBy { idToParticipantMapping(it.participantId).lastName }
        .sortedBy { it.routeCompletionTime!! } +
            groupResults.filter { it.routeCompletionTime == null }
                .sortedBy { idToParticipantMapping(it.participantId).lastName }
