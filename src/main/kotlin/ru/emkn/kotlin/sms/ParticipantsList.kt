package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.results_processing.FileContent

const val SIZE_OF_PARTICIPANT_LIST_ROW = 6

class ParticipantsList(val list: List<Participant>) : CsvDumpable {
    companion object : CreatableFromFileContent<ParticipantsList> {
        override fun readFromFileContent(fileContent: FileContent): ParticipantsList {
            fileContent.forEachIndexed { ind, row ->
                if (row.count { it == ',' } != SIZE_OF_PARTICIPANT_LIST_ROW)
                    throw IllegalArgumentException(
                        "The line number $ind in fileContent has incorrect number of commas! " +
                                "Should be $SIZE_OF_PARTICIPANT_LIST_ROW."
                    )
                val splittedRow = row.split(',')
                requireNotNull(splittedRow[0].toIntOrNull()) { "First argument(ID) of participant in line $ind is not a number!" }
                requireNotNull(splittedRow[1].toIntOrNull()) { "Second argument(age) of participant in line $ind is not a number!" }
            }
            return ParticipantsList(fileContent.map { row ->
                val splittedRow = row.split(',')
                Participant(
                    splittedRow[0].toInt(),
                    splittedRow[1].toInt(),
                    splittedRow[2],
                    splittedRow[3],
                    splittedRow[4],
                    splittedRow[5],
                    splittedRow[6]
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