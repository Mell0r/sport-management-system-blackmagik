package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.results_processing.FileContent

const val SIZE_OF_PARTICIPANT_LIST_ROW = 6

class ParticipantsList(val list: List<Participant>) : CsvDumpable {
    companion object : CreatableFromFileContentAndCompetition<ParticipantsList> {
        override fun readFromFileContentAndCompetition(fileContent: FileContent, competition: Competition): ParticipantsList {
            return ParticipantsList(fileContent.mapIndexed { ind, row ->
                val splitRow = row.split(',')
                if (row.count { it == ',' } != SIZE_OF_PARTICIPANT_LIST_ROW)
                    throw IllegalArgumentException(
                        "The line number $ind in fileContent has incorrect number of commas! " +
                                "Should be $SIZE_OF_PARTICIPANT_LIST_ROW."
                    )
                val id = splitRow[0].toIntOrNull()
                requireNotNull(id) { "First argument(ID) of participant in line $ind is not a number!" }
                val age = splitRow[1].toIntOrNull()
                requireNotNull(age) { "Second argument(age) of participant in line $ind is not a number!" }
                val groupLabel = splitRow[4]
                val group = competition.getGroupByLabelOrNull(groupLabel)
                requireNotNull(group) {
                    "Invalid group label \"$groupLabel\" of participant in line $ind. No group with such label exist."
                }
                Participant(
                    id,
                    age,
                    splitRow[2],
                    splitRow[3],
                    group,
                    splitRow[5],
                    splitRow[6]
                )
            })
        }
    }

    fun getParticipantById(id: Int) = list.find { it.id == id }

    override fun dumpToCsv() = list.map { it.toString() }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ParticipantsList

        return list.containsAll(other.list) && other.list.containsAll(list)
    }

    override fun hashCode(): Int {
        return list.hashCode()
    }
}