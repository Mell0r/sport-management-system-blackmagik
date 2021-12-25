package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.csv.CsvDumpable
import ru.emkn.kotlin.sms.startcfg.StartingProtocol

class ParticipantsList(val list: List<Participant>) : CsvDumpable {
    fun getParticipantById(id: Int) = list.find { it.id == id }

    override fun dumpToCsv() = list.map { it.dumpToCsvString() }
    override fun defaultCsvFileName() = "participants-list.csv"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ParticipantsList

        return list.containsAll(other.list) && other.list.containsAll(list)
    }

    override fun hashCode(): Int = list.hashCode()

    fun toStartingProtocols(): List<StartingProtocol> =
        list.groupBy { it.group }.map { (group, participants) ->
            StartingProtocol(group, participants)
        }
}