package ru.emkn.kotlin.sms

class ParticipantsList(val list : List<Participant>) {
    fun read(filePath: String) {
        TODO()
    }

    fun getParticipantById(id: Int) = list.find { it.id == id }

    fun print(filePath : String) {
        TODO()
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