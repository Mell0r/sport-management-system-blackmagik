package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.results_processing.FileContent
import ru.emkn.kotlin.sms.time.Time

typealias Score = Int

data class ParticipantAndTime(
    val participant: Participant,
    val totalTime: Time? // null if disqualified
)

class GroupResultProtocol(
    val groupName: GroupLabelT,
    val entries: List<ParticipantAndTime>
    // sorted by placeInGroup
) : CsvDumpable {
    companion object : CreatableFromFileContent<GroupResultProtocol> {
        override fun readFromFileContent(fileContent: FileContent): GroupResultProtocol {
            TODO("Not yet implemented")
        }
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
        return listOf(groupName) + PlayersPrinter(listOf(
            FieldInfo<Int>("Место") { ++index; places[index].toString() },
            FieldInfo("Индивидуальный номер") { id: Int -> id.toString() },
            FieldInfo("Результат") { id ->
                entries.first { it.participant.id == id }.totalTime?.toString()
                    ?: "снят"
            }
        )).toTable(entries.map { it.participant.id })

    }

    private fun generatePlaces(): List<Int> {
        val places = (1..entries.size).toMutableList()
        for (i in 0 until entries.lastIndex) {
            if (entries[i].totalTime == entries[i + 1].totalTime)
                places[i + 1] = places[i]
        }
        return places
    }
}