package ru.emkn.kotlin.sms

class GroupRequirement(private val ageFrom: Int, private val ageTo: Int) {
    fun checkApplicant(age : Int) = (age >= ageFrom) && (age <= ageTo)
}

class Competition(
    val discipline: String,
    val name: String,
    val year: Int,
    val date: String,
    val groups: List<String>,
    val routes: List<Route>,
    val groupToRouteMapping: Map<String, Route>,
    val requirementByGroup: Map<String, GroupRequirement>
)