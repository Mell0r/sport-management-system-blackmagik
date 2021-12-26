package ru.emkn.kotlin.sms

import org.jetbrains.exposed.sql.statements.InsertStatement
import ru.emkn.kotlin.sms.db.schema.RoutesTable
import ru.emkn.kotlin.sms.results_processing.CheckpointAndTime
import ru.emkn.kotlin.sms.results_processing.LiveParticipantResult
import ru.emkn.kotlin.sms.time.Time

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
                Integer.max(0, checkpointsToTimes.size - orderedCheckpoints.size)
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

    override fun RoutesTable.initializeTableRow(statement: InsertStatement<Number>) {
        statement[id] = this@OrderedCheckpointsRoute.name
        statement[type] = RouteType.ORDERED_CHECKPOINTS
        statement[commaSeparatedCheckpoints] = this@OrderedCheckpointsRoute.orderedCheckpoints.joinToString(",")
        statement[threshold] = null
    }

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

