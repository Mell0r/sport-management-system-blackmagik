package ru.emkn.kotlin.sms

abstract class Group(val label: String, val route: Route) {
    abstract fun checkParticipantValidity(participant: Participant) : Boolean
    override fun toString() = label
}

class AgeGroup(label: String, route: Route, private val ageFrom: Int, private val ageTo: Int) : Group(label, route) {
    override fun checkParticipantValidity(participant: Participant): Boolean {
        return participant.age in ageFrom..ageTo
    }
}