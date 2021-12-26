package ru.emkn.kotlin.sms

import org.jetbrains.exposed.sql.statements.InsertStatement
import ru.emkn.kotlin.sms.csv.CsvStringDumpable
import ru.emkn.kotlin.sms.db.schema.ParticipantsListTable
import ru.emkn.kotlin.sms.db.util.RecordableToTableRow
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
) : CsvStringDumpable, RecordableToTableRow<ParticipantsListTable> {
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

    override fun ParticipantsListTable.initializeTableRow(statement: InsertStatement<Number>) {
        statement[id] =             this@Participant.id
        statement[age] =            this@Participant.age
        statement[name] =           this@Participant.name
        statement[lastName] =       this@Participant.lastName
        statement[group] =          this@Participant.group.label
        statement[team] =           this@Participant.team
        statement[sportsCategory] = this@Participant.sportsCategory
        statement[startingTime] =   this@Participant.startingTime
    }
}
