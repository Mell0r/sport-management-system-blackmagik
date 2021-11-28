package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.results_processing.FileContent

class ParticipantsList(val list : List<Participant>) : CsvDumpable {

    fun getParticipantById(id: Int) = list.find { it.id == id }

    fun getFileName() = "Participant_list.csv"

    override fun dumpToCsv() = list.map { it.toString() }

    override fun equals(other: Any?) : Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ParticipantsList

        return list.containsAll(other.list) && other.list.containsAll(list)
    }

    override fun hashCode(): Int {
        return list.hashCode()
    }
}