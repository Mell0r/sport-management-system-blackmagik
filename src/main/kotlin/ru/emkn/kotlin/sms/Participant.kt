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
    constructor(
        age: Int,
        name: String,
        lastName: String,
        supposedGroup: GroupLabelT,
        team: String,
        sportsCategory: String
    ) : this(
        counter++,
        age,
        name,
        lastName,
        supposedGroup,
        team,
        sportsCategory
    ) {
    }

    companion object {
        var counter: Int = 0
    }

    override fun toString() =
        "$id,$age,$name,$lastName,$supposedGroup,$team,$sportsCategory"
}