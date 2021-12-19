package ru.emkn.kotlin.sms.gui.builders

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import ru.emkn.kotlin.sms.Application

class ApplicationBuilder(
    var team: MutableState<String> = mutableStateOf(""),
    val applicants: SnapshotStateList<ApplicantBuilder> = mutableStateListOf()
) {
    companion object {
        fun fromApplication(application: Application) = ApplicationBuilder(
            team = mutableStateOf(application.teamName),
            applicants = application
                .applicantsList.map { applicant ->
                    ApplicantBuilder.fromApplicant(applicant)
                }
                .toMutableStateList()
        )
    }

    /**
     * @throws [IllegalArgumentException] if it could not create some applicant.
     */
    fun build(): Application {
        val teamName = team.value
        val actualApplicants = applicants.map {
            try {
                it.build(teamName)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Could not form applicant ${it}: ${e.message}")
            }
        }
        return Application(
            teamName = teamName,
            applicantsList = actualApplicants,
        )
    }
}

