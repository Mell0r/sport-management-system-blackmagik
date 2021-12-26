package ru.emkn.kotlin.sms

import org.jetbrains.exposed.sql.statements.InsertStatement
import ru.emkn.kotlin.sms.db.GroupsTable
import ru.emkn.kotlin.sms.startcfg.Applicant

class AgeGroup(
    label: String,
    route: Route,
    val ageFrom: Int,
    val ageTo: Int,
    private val competitionYear: Int,
) : Group(label, route) {

    override fun checkApplicantValidity(applicant: Applicant): Boolean =
        applicant.getAge(competitionYear) in ageFrom..ageTo

    override fun dumpToCsvString(): String = "$label,$ageFrom,$ageTo"

    override fun GroupsTable.initializeTableRow(statement: InsertStatement<Number>) {
        statement[id] = this@AgeGroup.label
        statement[route] = this@AgeGroup.route.name
        statement[type] = GroupType.AGE
        statement[ageFrom] = this@AgeGroup.ageFrom
        statement[ageTo] = this@AgeGroup.ageTo
    }
}
