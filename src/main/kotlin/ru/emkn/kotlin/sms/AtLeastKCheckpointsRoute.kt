package ru.emkn.kotlin.sms

import org.jetbrains.exposed.sql.statements.InsertStatement
import ru.emkn.kotlin.sms.db.schema.RoutesTable
import ru.emkn.kotlin.sms.results_processing.CheckpointAndTime
import ru.emkn.kotlin.sms.results_processing.LiveParticipantResult
import ru.emkn.kotlin.sms.time.Time

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

    override fun RoutesTable.initializeTableRow(statement: InsertStatement<Number>) {
        statement[id] = this@AtLeastKCheckpointsRoute.name
        statement[type] = RouteType.AT_LEAST_K_CHECKPOINTS
        statement[commaSeparatedCheckpoints] = this@AtLeastKCheckpointsRoute.checkpoints.joinToString(",")
        statement[threshold] = this@AtLeastKCheckpointsRoute.threshold
    }

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
