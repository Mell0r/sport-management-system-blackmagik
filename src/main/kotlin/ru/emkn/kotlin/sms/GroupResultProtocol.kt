package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.results_processing.FileContent
import ru.emkn.kotlin.sms.time.Time

data class ParticipantAndTime(
    val id: Int,
    val totalTime: Time? // null if disqualified
)

class GroupResultProtocol(
    val group: Group,
    val entries: List<ParticipantAndTime>
    // sorted by placeInGroup
) : CsvDumpable {
    companion object : CreatableFromFileContentAndCompetition<GroupResultProtocol> {
        override fun readFromFileContentAndCompetition(fileContent: FileContent, competition: Competition): GroupResultProtocol {
            val groupName = fileContent[0].split(",").first()
            val group = competition.getGroupByLabelOrNull(groupName)
                ?: logErrorAndThrow("No group with name \"$groupName\" exist.")
            val rest = fileContent.drop(2) // group row and header row
            val participantAndTimeList = rest.mapIndexed { index, line ->
                val tokens = line.split(",")
                if (tokens.size != 3)
                    logErrorAndThrow("Line $line: not three comma separated values.")
                val (_, id, time) = tokens
                val idNum =
                    id.toIntOrNull() ?: logErrorAndThrow("Line $index: bad id.")
                val timeParsed = when (time) {
                    "снят" -> null
                    else -> Time.fromString(time)
                }
                ParticipantAndTime(
                    idNum,
                    timeParsed
                )
            }
            return GroupResultProtocol(group, participantAndTimeList)
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
        return listOf(group.label) + PlayersPrinter(listOf(
            FieldInfo("Место") { ++index; places[index].toString() },
            FieldInfo("Индивидуальный номер") { id: Int -> id.toString() },
            FieldInfo("Результат") { id ->
                entries.first { it.id == id }.totalTime?.toString()
                    ?: "снят"
            }
        )).toTable(entries.map { it.id })

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