package ru.emkn.kotlin.sms

abstract class Group(val label: String, var route: Route) {
    abstract fun checkParticipantValidity(participant: Participant) : Boolean
    override fun toString() = label
    abstract fun dumpToCsvString(): String // only requirement, without route
}

class AgeGroup(label: String, route: Route, val ageFrom: Int, val ageTo: Int) : Group(label, route) {
    override fun checkParticipantValidity(participant: Participant): Boolean {
        return participant.age in ageFrom..ageTo
    }

    override fun dumpToCsvString(): String {
        return "$label,$ageFrom,$ageTo"
    }
}