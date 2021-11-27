package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.results_processing.FileContent

class ParticipantsList(val list : List<Participant>) : CsvDumpable {
    companion object : CreatableFromFileContent<ParticipantsList>{
        override fun readFromFileContent(fileContent: FileContent): ParticipantsList {
            TODO("Not yet implemented")
        }

    }
    fun read(filePath: String) {
        TODO()
    }

    fun getParticipantById(id: Int) = list.find { it.id == id }

    fun print(filePath : String) {
        TODO()
    }

    override fun dumpToCsv(): FileContent {
        TODO("Not yet implemented")
    }

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