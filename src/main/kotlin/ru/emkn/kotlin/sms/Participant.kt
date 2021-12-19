package ru.emkn.kotlin.sms

data class Participant(
    val id: Int,
    var age: Int,
    var name: String,
    var lastName: String,
    var group: Group,
    var team: String,
    var sportsCategory: String // from fixed options from config
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
    )

    companion object {
        var counter: Int = 0
    }

    override fun toString() =
        "$id,$age,$name,$lastName,$group,$team,$sportsCategory"
}