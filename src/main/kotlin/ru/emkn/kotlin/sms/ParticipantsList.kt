package ru.emkn.kotlin.sms

class ParticipantsList(val list : List<Participant>) {
    fun read(filePath: String) {
        TODO()
    }

    fun getParticipantById(id: Int) = list.find { it.id == id }

    fun print(filePath : String) {
        TODO()
    }
}