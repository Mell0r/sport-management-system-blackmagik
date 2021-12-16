package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.results_processing.FileContent
import ru.emkn.kotlin.sms.time.Time

data class IdWithFinalResult(
    val id: Int,
    val result: FinalParticipantResult,
)

data class IdWithLiveResult(
    val id: Int,
    val result: LiveParticipantResult,
) {
    fun toIdWithFinalResult() = IdWithFinalResult(id, result.toFinalParticipantResult())
}

class GroupResultProtocol(
    val group: Group,
    val entries: List<IdWithFinalResult>
    // sorted by placeInGroup
) : CsvDumpable {
    companion object : CreatableFromFileContentAndCompetition<GroupResultProtocol> {
        override fun readFromFileContentAndCompetition(fileContent: FileContent, competition: Competition): GroupResultProtocol {
            val groupName = fileContent[0].split(",").first()
            val group = competition.getGroupByLabelOrNull(groupName)
                ?: logErrorAndThrow("No group with name \"$groupName\" exist.")
            val rest = fileContent.drop(2) // group row and header row
            val participantAndTimeList = rest.mapIndexed { index, row ->
                try {
                    readIdWithFinalResultFromRow(row)
                } catch (e: IllegalArgumentException) {
                    val lineNumber =
                        index + 3 // 3 = 1 for zero-based indexing + 2 for the first two lines being dropped
                    val messageWithLineNumber = "Line $lineNumber: ${e.message}"
                    logErrorAndThrow(messageWithLineNumber)
                }
            }
            return GroupResultProtocol(group, participantAndTimeList)
        }

        private fun readIdWithFinalResultFromRow(row: String): IdWithFinalResult {
            val tokens = row.split(",")
            if (tokens.size != 3)
                logErrorAndThrow("Not three comma separated values.")
            val (_, id, time) = tokens
            val idNum =
                id.toIntOrNull() ?: logErrorAndThrow("Bad id.")
            val result = when (time) {
                "снят" -> FinalParticipantResult.Disqualified()
                else -> FinalParticipantResult.Finished(Time.fromString(time))
            }
            return IdWithFinalResult(
                idNum,
                result,
            )
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
                entries.first { it.id == id }.result.dumpToCsvString()
            }
        )).toTable(entries.map { it.id })

    }

    private fun generatePlaces(): List<Int> {
        val places = (1..entries.size).toMutableList()
        for (i in 0 until entries.lastIndex) {
            if (entries[i].result == entries[i + 1].result)
                places[i + 1] = places[i]
        }
        return places
    }
}