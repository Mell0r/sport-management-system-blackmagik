package ru.emkn.kotlin.sms.gui.builders

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import ru.emkn.kotlin.sms.startcfg.Applicant

class ApplicantBuilder(
    val supposedGroupLabel: MutableState<String> = mutableStateOf(""),
    val lastName: MutableState<String> = mutableStateOf(""),
    val name: MutableState<String> = mutableStateOf(""),
    val birthYear: MutableState<String> = mutableStateOf(""),
    val sportsCategory: MutableState<String> = mutableStateOf(""),
) {
    companion object {
        fun fromApplicant(applicant: Applicant) = ApplicantBuilder(
            supposedGroupLabel = mutableStateOf(applicant.supposedGroupLabel),
            lastName = mutableStateOf(applicant.lastName),
            name = mutableStateOf(applicant.name),
            birthYear = mutableStateOf(applicant.birthYear.toString()),
            sportsCategory = mutableStateOf(applicant.sportsCategory),
        )
    }

    /**
     * @throws [IllegalArgumentException] if [birthYear] is not a number
     */
    fun build(teamName: String): Applicant {
        val actualBirthYear = birthYear.value.toIntOrNull()
            ?: throw IllegalArgumentException("Birth year (${birthYear.value}) is not a number!")
        return Applicant(
            supposedGroupLabel = supposedGroupLabel.value,
            lastName = lastName.value,
            name = name.value,
            birthYear = actualBirthYear,
            teamName = teamName,
            sportsCategory = sportsCategory.value,
        )
    }
}

