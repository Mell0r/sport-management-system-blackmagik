package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.results_processing.FileContent

const val SIZE_OF_PARTICIPANT_LIST_ROW = 6

class ParticipantsList(val list: List<Participant>) : CsvDumpable {
    companion object :
        CreatableFromFileContentAndCompetition<ParticipantsList> {
        override fun readFromFileContentAndCompetition(
            fileContent: FileContent,
            competition: Competition
        ): ParticipantsList {
            return ParticipantsList(fileContent.mapIndexed { index, row ->
                try {
                    readParticipantFromRow(row, competition)
                } catch (e: IllegalArgumentException) {
                    val lineNumber = index + 1
                    val messageWithLineNumber = "Line $lineNumber: ${e.message}"
                    logErrorAndThrow(messageWithLineNumber)
                }
            })
        }

        private fun readParticipantFromRow(
            row: String,
            competition: Competition
        ): Participant {
            val tokens = row.split(',')
            require(row.count { it == ',' } == SIZE_OF_PARTICIPANT_LIST_ROW) {
                "Incorrect number of commas! " +
                        "Should be $SIZE_OF_PARTICIPANT_LIST_ROW."
            }
            val id = tokens[0].toIntOrThrow(
                IllegalArgumentException("First argument(ID) of participant is not a number!")
            )
            val age = tokens[1].toIntOrNull()
            requireNotNull(age) { "Second argument(age) of participant is not a number!" }
            val groupLabel = tokens[4]
            val group = competition.getGroupByLabelOrNull(groupLabel)
            requireNotNull(group) {
                "Invalid group label \"$groupLabel\" of participant. No group with such label exist."
            }
            return Participant(
                id,
                age,
                tokens[2],
                tokens[3],
                group,
                tokens[5],
                tokens[6]
            )
        }
    }

    fun getParticipantById(id: Int) = list.find { it.id == id }

    override fun dumpToCsv() = list.map { "$it" }
    override fun defaultCsvFileName() = "participants-list.csv"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ParticipantsList

        return list.containsAll(other.list) && other.list.containsAll(list)
    }

    override fun hashCode(): Int = list.hashCode()
}