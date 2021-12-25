package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.csv.CsvDumpable
import ru.emkn.kotlin.sms.io.FileContent

data class GroupResultProtocol(
    val group: Group,
    val entries: List<ParticipantWithFinalResult>
    // sorted by placeInGroup
) : CsvDumpable {

    init {
        require(entries == entries.sortedBy { it.result })
    }

    override fun dumpToCsv(): FileContent {
        data class FieldInfo<T>(
            val fieldName: String,
            val generateFieldValue: (T) -> String
        )

        class PlayersPrinter<T>(
            private val fieldsInfo: List<FieldInfo<T>>
        ) {
            fun toTable(values: List<T>): List<String> {
                val headers =
                    listOf(fieldsInfo.joinToString(",") { it.fieldName })
                val fieldValuesTable = values.map { value ->
                    fieldsInfo.joinToString(",") {
                        it.generateFieldValue(value)
                    }
                }
                return headers + fieldValuesTable
            }
        }

        val places = generatePlaces()
        var index = -1
        return listOf(group.label) + PlayersPrinter(listOf(
            FieldInfo<ParticipantWithFinalResult>("Место") { ++index; "${places[index]}" },
            FieldInfo("Индивидуальный номер") { (participant, _) ->
                "${participant.id}"
            },
            FieldInfo("Результат") { (_, result) ->
                result.dumpToCsvString()
            }
        )).toTable(entries)
    }

    override fun defaultCsvFileName() = "result-of-group-${group.label}.csv"

    private fun generatePlaces(): List<Int> {
        val places = (1..entries.size).toMutableList()
        (0 until entries.lastIndex)
            .filter { entries[it].result == entries[it + 1].result }
            .forEach { places[it + 1] = places[it] }
        return places
    }
}