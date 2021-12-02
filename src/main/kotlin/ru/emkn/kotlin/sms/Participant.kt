package ru.emkn.kotlin.sms

typealias GroupLabelT = String

data class Participant(
    val id: Int,
    val age: Int,
    val name: String,
    val lastName: String,
    val supposedGroup: GroupLabelT,
    val team: String,
    val sportsCategory: String // from fixed options from config
) {
    override fun toString() =
        "$id,$age,$name,$lastName,$supposedGroup,$team,$sportsCategory"
}