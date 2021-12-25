package ru.emkn.kotlin.sms.startcfg

/**
 * An [Applicant] is somebody who is about to become a [ProcessedApplicant]
 * (in case he is admitted to competition).
 *
 * He does not have an id yet, and he does not have a specified starting time.
 * Also, [birthYear] is stored instead of an age.
 */
data class Applicant(
    val supposedGroupLabel: String,
    val lastName: String,
    val name: String,
    val birthYear: Int,
    val teamName: String,
    val sportsCategory: String,
) {
    fun getAge(currentYear: Int): Int {
        return currentYear - birthYear
    }
}

