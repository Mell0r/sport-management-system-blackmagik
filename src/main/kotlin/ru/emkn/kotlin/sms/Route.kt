package ru.emkn.kotlin.sms

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.time.Time
import java.lang.Integer.max

typealias CheckpointLabelT = String

sealed class Route(val name: String) : CsvStringDumpable {
    abstract val checkpoints: Set<CheckpointLabelT>

    fun calculateFinalResult(
        checkpointsToTimes: List<CheckpointAndTime>,
        startingTime: Time
    ): FinalParticipantResult = calculateLiveResult(checkpointsToTimes, startingTime).toFinalParticipantResult()

    abstract fun calculateLiveResult(
        checkpointsToTimes: List<CheckpointAndTime>,
        startingTime: Time
    ): LiveParticipantResult
}

/*
The line must start with a route type id surrounded with dollar signs.
The $0$ is optional for backwards compatibility.
Example (ChP stands for checkpoint here):
$0$orderedRouteName,firstChP, secondChP,thirdChP
$1$atLeastKRouteName,k,firstChP,secondChP,thirdChP
 */
@kotlin.ExperimentalStdlibApi
fun readRouteFromLine(line: String): Route {
    if (!line.startsWith("\$"))
        return readOrderedRouteCheckpoint(line)
    val match = """\$(\d+)\$""".toRegex().matchAt(line, 0)
        ?: throw IllegalArgumentException("Bad format: there is no second dollar sign in line.")
    val prefixLength =
        match.range.last - match.range.first + 1 // plus one as both ends should be included
    val clearLine = line.drop(prefixLength)
    val routeTypeId = match.groups[1]?.value?.toInt()
        ?: throw InternalError("The regex in readRouteFromLine is broken.")
    return when (routeTypeId) {
        0 -> readOrderedRouteCheckpoint(clearLine)
        1 -> readAtLeastKCheckpointsRoute(clearLine)
        else -> throw IllegalArgumentException("Unsupported route id: $routeTypeId")
    }
}

private fun readAtLeastKCheckpointsRoute(line: String): AtLeastKCheckpointsRoute {
    val tokens = line.split(',').filter { it.isNotEmpty() }
    if (tokens.isEmpty())
        throw IllegalArgumentException("Empty line in 'Route_description.")
    val name = tokens[0]
    val k = tokens[1].toIntOrNull()
        ?: throw IllegalArgumentException("Bad k (not a number): ${tokens[1]}")
    val droppedNameAndK = tokens.drop(2)
    val checkpoints = droppedNameAndK.toMutableSet()
    return AtLeastKCheckpointsRoute(name, checkpoints, k)
}

private fun readOrderedRouteCheckpoint(line: String): OrderedCheckpointsRoute {
    val tokens = line.split(',').filter { it.isNotEmpty() }
    require(tokens.isNotEmpty()) { "Empty line in 'Route_description." }
    return OrderedCheckpointsRoute(tokens[0], tokens.drop(1).toMutableList())
}

class OrderedCheckpointsRoute(
    name: String,
    val orderedCheckpoints: MutableList<CheckpointLabelT>,
) : Route(name) {
    override val checkpoints: Set<CheckpointLabelT>
        get() = orderedCheckpoints.toSet()

    override fun calculateLiveResult(
        checkpointsToTimes: List<CheckpointAndTime>,
        startingTime: Time
    ): LiveParticipantResult {
        if (checkpointsToTimes.minOf { it.time } < startingTime) {
            // Finished earlier than started
            // Disqualifying
            return LiveParticipantResult.Disqualified()
        }

        val checkpointsToTimesChronological = checkpointsToTimes
            .sortedBy { it.time }
            .dropLast(max(0, checkpointsToTimes.size - orderedCheckpoints.size)) // remove checkpoints after the last one
        val chronologicalCheckpoints =
            checkpointsToTimesChronological.map { it.checkpointLabel }
        val lastCheckpointTime = checkpointsToTimesChronological.last().time

        return if (chronologicalCheckpoints == orderedCheckpoints) {
            // Finished
            LiveParticipantResult.Finished(Time(lastCheckpointTime - startingTime))
        } else if (chronologicalCheckpoints.size < orderedCheckpoints.size &&
            chronologicalCheckpoints == orderedCheckpoints.dropLast(orderedCheckpoints.size - chronologicalCheckpoints.size)) {
            // [chronologicalCheckpoints] is a strict prefix of [orderedCheckpoints]
            // Participant is in process
            LiveParticipantResult.InProcess(chronologicalCheckpoints.size, Time(lastCheckpointTime - startingTime))
        } else {
            // Passed checkpoints in wrong order
            LiveParticipantResult.Disqualified()
        }
    }

    private fun logFalseStartWarning(
        checkpointsToTimes: List<CheckpointAndTime>,
        startingTime: Time
    ) {
        Logger.warn {
            "Current participant passed his first checkpoint (at ${checkpointsToTimes.minOf { it.time }}) before he is supposed to start (${
                startingTime
            }). Disqualifying."
        }
    }

    private fun logDisqualificationWarning(chronologicalCheckpoints: List<CheckpointLabelT>) {
        Logger.warn {
            "Current participant passed checkpoints in wrong order (expected: ${
                orderedCheckpoints
            }, actual: $chronologicalCheckpoints). Disqualifying."
        }
    }

    override fun dumpToCsvString(): String {
        return "$0$${name}," + orderedCheckpoints.joinToString(",")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OrderedCheckpointsRoute

        if (orderedCheckpoints != other.orderedCheckpoints) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode() + 31 * orderedCheckpoints.hashCode()
    }
}

class AtLeastKCheckpointsRoute(
    name: String,
    override val checkpoints: MutableSet<CheckpointLabelT>,
    var threshold: Int,
) : Route(name) {
    init {
        require(threshold <= checkpoints.size) { "k must not be greater than the number of checkpoints." }
    }

    override fun calculateLiveResult(
        checkpointsToTimes: List<CheckpointAndTime>,
        startingTime: Time
    ): LiveParticipantResult {
        if (checkpointsToTimes.minOf { it.time } < startingTime) {
            // Finished earlier than started
            // Disqualifying
            return LiveParticipantResult.Disqualified()
        }

        val visitedCheckpointFromRoute = checkpointsToTimes
            .filter { it.checkpointLabel in checkpoints }
            .sortedBy { it.time }
            .distinctBy { it.checkpointLabel }

        if (visitedCheckpointFromRoute.size < threshold) {
            // In process
            return LiveParticipantResult.InProcess(
                completedCheckpoints = visitedCheckpointFromRoute.size,
                lastCheckpointTime = visitedCheckpointFromRoute.last().time,
            )
        }

        // Finished
        val lastRelevantCheckpoint = visitedCheckpointFromRoute[threshold - 1]
        return LiveParticipantResult.Finished(
            Time(lastRelevantCheckpoint.time - startingTime)
        )
    }

    override fun dumpToCsvString(): String {
        return "$1$${name},${threshold}," + checkpoints.joinToString(",")
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