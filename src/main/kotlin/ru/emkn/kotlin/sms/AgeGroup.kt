package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.db.GroupEntity
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

    override fun toEntity(): GroupEntity {
        return GroupEntity.new(label) {
            route = this@AgeGroup.route.name
            type = GroupType.AGE
            ageFrom = this@AgeGroup.ageFrom
            ageTo = this@AgeGroup.ageTo
        }
    }
}
