package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.csv.CsvStringDumpable
import ru.emkn.kotlin.sms.results_processing.CheckpointAndTime
import ru.emkn.kotlin.sms.results_processing.FinalParticipantResult
import ru.emkn.kotlin.sms.results_processing.LiveParticipantResult
import ru.emkn.kotlin.sms.time.Time
import java.lang.Integer.max

typealias CheckpointLabelT = String

sealed class Route(val name: String) : CsvStringDumpable {
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

class OrderedCheckpointsRoute(
    name: String,
    val orderedCheckpoints: List<CheckpointLabelT>,
) : Route(name) {
    override val checkpoints: Set<CheckpointLabelT>
        get() = orderedCheckpoints.toSet()

    override fun calculateLiveResult(
        checkpointsToTimes: List<CheckpointAndTime>,
        startingTime: Time
    ): LiveParticipantResult {
        val firstTimestamp = checkpointsToTimes.minOfOrNull { it.time }
        val madeAFalseStart =
            firstTimestamp != null && firstTimestamp < startingTime
        if (madeAFalseStart) {
            return LiveParticipantResult.Disqualified()
        }

        val checkpointsToTimesChronological = checkpointsToTimes
            .sortedBy { it.time }
            .dropLast(
                max(0, checkpointsToTimes.size - orderedCheckpoints.size)
            ) // remove checkpoints after the last one
        val chronologicalCheckpoints =
            checkpointsToTimesChronological.map { it.checkpointLabel }
        val lastCheckpointTime =
            checkpointsToTimesChronological.lastOrNull()?.time

        return when {
            lastCheckpointTime == null -> LiveParticipantResult.InProcess(
                0,
                Time(0)
            )
            chronologicalCheckpoints == orderedCheckpoints -> {
                LiveParticipantResult.Finished(lastCheckpointTime - startingTime)
            }
            chronologicalCheckpoints.isStrictPrefixOf(orderedCheckpoints) -> {
                LiveParticipantResult.InProcess(
                    chronologicalCheckpoints.size,
                    lastCheckpointTime - startingTime
                )
            }
            else -> {
                // Passed checkpoints in wrong order
                LiveParticipantResult.Disqualified()
            }
        }
    }

    override fun dumpToCsvString(): String =
        "$0$${name}," + orderedCheckpoints.joinToString(",")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OrderedCheckpointsRoute

        if (orderedCheckpoints != other.orderedCheckpoints) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int =
        name.hashCode() + 31 * orderedCheckpoints.hashCode()

    private fun <T> List<T>.isStrictPrefixOf(other: List<T>) =
        this.size < other.size && this == other.dropLast(other.size - this.size)
}

class AtLeastKCheckpointsRoute(
    name: String,
    override val checkpoints: Set<CheckpointLabelT>,
    val threshold: Int,
) : Route(name) {
    init {
        require(threshold <= checkpoints.size) { "k must not be greater than the number of checkpoints." }
    }

    override fun calculateLiveResult(
        checkpointsToTimes: List<CheckpointAndTime>,
        startingTime: Time
    ): LiveParticipantResult {
        val firstTimestamp = checkpointsToTimes.minOfOrNull { it.time }
        val madeAFalseStart =
            firstTimestamp != null && firstTimestamp < startingTime
        if (madeAFalseStart) {
            return LiveParticipantResult.Disqualified()
        }

        val visitedCheckpointFromRoute = checkpointsToTimes
            .filter { it.checkpointLabel in checkpoints }
            .sortedBy { it.time }
            .distinctBy { it.checkpointLabel }

        if (visitedCheckpointFromRoute.size < threshold) {
            return LiveParticipantResult.InProcess(
                completedCheckpoints = visitedCheckpointFromRoute.size,
                lastCheckpointTime = visitedCheckpointFromRoute.lastOrNull()?.time
                    ?: Time(0),
            )
        }

        val lastRelevantCheckpoint = visitedCheckpointFromRoute[threshold - 1]
        return LiveParticipantResult.Finished(
            lastRelevantCheckpoint.time - startingTime
        )
    }

    override fun dumpToCsvString(): String =
        "$1$${name},${threshold}," + checkpoints.joinToString(",")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AtLeastKCheckpointsRoute

        if (checkpoints != other.checkpoints) return false
        if (threshold != other.threshold) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = checkpoints.hashCode()
        result = 31 * result + threshold
        result = 31 * result + name.hashCode()
        return result
    }
}