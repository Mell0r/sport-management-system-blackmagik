package ru.emkn.kotlin.sms

abstract class Group(val label: String, var route: Route) : CsvStringDumpable {
    abstract fun checkParticipantValidity(participant: Participant): Boolean
    override fun toString() = label
}

class AgeGroup(label: String, route: Route, val ageFrom: Int, val ageTo: Int) :
    Group(label, route) {
    override fun checkParticipantValidity(participant: Participant): Boolean =
        participant.age in ageFrom..ageTo

    override fun dumpToCsvString(): String = "$label,$ageFrom,$ageTo"
}