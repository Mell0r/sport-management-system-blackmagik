package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.CheckpointLabelT
import ru.emkn.kotlin.sms.time.Time

data class CheckpointAndTime(
    val checkpointLabel: CheckpointLabelT,
    val time: Time
)

