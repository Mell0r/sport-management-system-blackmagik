package ru.emkn.kotlin.sms

typealias GroupLabelT = String

class Participant(
    val id: Int,
    val age: Int,
    val name: String,
    val lastName: String,
    val supposedGroup: GroupLabelT,
    val team: String,
    val sportsCategory: String // from fixed options from config
)

fun readFromApplication(rawCSV: List<List<String>>): List<Participant> = TODO();
