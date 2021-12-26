package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.csv.CsvStringDumpable
import ru.emkn.kotlin.sms.db.schema.RoutesTable
import ru.emkn.kotlin.sms.db.util.RecordableToTableRow
import ru.emkn.kotlin.sms.results_processing.CheckpointAndTime
import ru.emkn.kotlin.sms.results_processing.FinalParticipantResult
import ru.emkn.kotlin.sms.results_processing.LiveParticipantResult
import ru.emkn.kotlin.sms.time.Time

sealed class Route(val name: String) : CsvStringDumpable, RecordableToTableRow<RoutesTable> {
    abstract val checkpoints: Set<CheckpointLabelT>

    fun calculateFinalResult(
        checkpointsToTimes: List<CheckpointAndTime>,
        startingTime: Time
    ): FinalParticipantResult = calculateLiveResult(
        checkpointsToTimes,
        startingTime
    ).toFinalParticipantResult()

    abstract fun calculateLiveResult(
        checkpointsToTimes: List<CheckpointAndTime>,
        startingTime: Time
    ): LiveParticipantResult
}
