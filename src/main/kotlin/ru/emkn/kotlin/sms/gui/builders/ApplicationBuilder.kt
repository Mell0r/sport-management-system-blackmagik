package ru.emkn.kotlin.sms.gui.builders

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

class ApplicationBuilder(
    var team: String = "",
    val applicants: SnapshotStateList<ApplicantBuilder> = mutableStateListOf()
)

