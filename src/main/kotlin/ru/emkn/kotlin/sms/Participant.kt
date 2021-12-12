package ru.emkn.kotlin.sms

data class Participant(
    val id: Int,
    val age: Int,
    val name: String,
    val lastName: String,
    val group: Group,
    val team: String,
    val sportsCategory: String // from fixed options from config
) {
    constructor(
        age: Int,
        name: String,
        lastName: String,
        group: Group,
        team: String,
        sportsCategory: String
    ) : this(
        counter++,
        age,
        name,
        lastName,
        group,
        team,
        sportsCategory
    ) {
    }

    companion object {
        var counter: Int = 0
    }

    override fun toString() =
        "$id,$age,$name,$lastName,$group,$team,$sportsCategory"
}