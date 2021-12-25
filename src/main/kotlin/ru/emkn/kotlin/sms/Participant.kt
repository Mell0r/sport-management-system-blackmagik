package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.csv.CsvStringDumpable
import ru.emkn.kotlin.sms.db.ConvertibleToDBEntity
import ru.emkn.kotlin.sms.db.ParticipantEntity
import ru.emkn.kotlin.sms.startcfg.ProcessedApplicant
import ru.emkn.kotlin.sms.time.Time

/**
 * A [Participant] is someone who participates in the competition
 * and has already been assigned with an [id] and his [startingTime].
 */
data class Participant(
    val id: Int,
    val age: Int,
    val name: String,
    val lastName: String,
    val group: Group,
    val team: String,
    val sportsCategory: String,
    val startingTime: Time,
) : CsvStringDumpable, ConvertibleToDBEntity<ParticipantEntity> {
    constructor(
        age: Int,
        name: String,
        lastName: String,
        group: Group,
        team: String,
        sportsCategory: String,
        startingTime: Time,
    ) : this(
        id = counter++,
        age = age,
        name = name,
        lastName = lastName,
        group = group,
        team = team,
        sportsCategory = sportsCategory,
        startingTime = startingTime,
    )

    constructor(processedApplicant: ProcessedApplicant, startingTime: Time) : this(
        id = counter++,
        age = processedApplicant.age,
        name = processedApplicant.name,
        lastName = processedApplicant.lastName,
        group = processedApplicant.group,
        team = processedApplicant.team,
        sportsCategory = processedApplicant.sportsCategory,
        startingTime = startingTime,
    )

    companion object {
        var counter: Int = 0
    }

    override fun toString() =
        "$id. $name $lastName"

    override fun dumpToCsvString() =
        "$id,$age,$name,$lastName,$group,$team,$sportsCategory,$startingTime"

    override fun ParticipantEntity.initializeEntity() {
        this@initializeEntity.age =            this@Participant.age
        this@initializeEntity.name =           this@Participant.name
        this@initializeEntity.lastName =       this@Participant.lastName
        this@initializeEntity.group =          this@Participant.group.label
        this@initializeEntity.team =           this@Participant.team
        this@initializeEntity.sportsCategory = this@Participant.sportsCategory
        this@initializeEntity.startingTime =   this@Participant.startingTime
    }
}
