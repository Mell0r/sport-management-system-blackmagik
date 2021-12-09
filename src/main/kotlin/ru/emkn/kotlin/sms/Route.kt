package ru.emkn.kotlin.sms

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.results_processing.CheckpointLabelAndTime
import ru.emkn.kotlin.sms.results_processing.CheckpointLabelT
import ru.emkn.kotlin.sms.time.Time

sealed class Route(val name: String, val checkpoints: Set<CheckpointLabelT>) {
    // null if disqualified
    abstract fun calculateResultingTime(
        checkpointsToTimes: List<CheckpointLabelAndTime>,
        startingTime: Time
    ): Time?
}

/*
The line must start with a route type id surrounded with dollar signs.
The $0$ is optional for backwards compatibility.
Example (ChP stands for checkpoint here):
$0$orderedRouteName,firstChP, secondChP,thirdChP
$1$atLeastKRouteName,k,firstChP,secondChP,thirdChP
 */

@ExperimentalStdlibApi
fun readRouteFromLine(line: String): Route {
    if (!line.startsWith("\$"))
        return readOrderedRouteCheckpoint(line)
    val match = """\$(\d+)\$""".toRegex().matchAt(line, 0)
        ?: throw IllegalArgumentException("Bad format: there is no second dollar sign in line")
    val prefixLength =
        match.range.last - match.range.first + 1 // plus one as both ends should be included
    val clearLine = line.drop(prefixLength)
    val routeTypeId = match.groups[1]!!.value.toInt()
    return when (routeTypeId) {
        0 -> readOrderedRouteCheckpoint(clearLine)
        1 -> readAtLeastKCheckpointsRoute(clearLine)
        else -> throw IllegalArgumentException("Unsupported route id: $routeTypeId")
    }
}

private fun readAtLeastKCheckpointsRoute(line: String): AtLeastKCheckpointsRoute {
    val splittedRow = line.split(',').filter { it.isNotEmpty() }
    if (splittedRow.isEmpty())
        throw IllegalArgumentException("Empty line in 'Route_description.")
    val name = splittedRow[0]
    val k = splittedRow[1].toIntOrNull()
        ?: throw IllegalArgumentException("Bad k: ${splittedRow[1]}")
    val checkpoints = splittedRow.drop(2).toSet()
    return AtLeastKCheckpointsRoute(name, checkpoints, k)
}

private fun readOrderedRouteCheckpoint(line: String): OrderedCheckpointsRoute {
    val splittedRow = line.split(',').filter { it.isNotEmpty() }
    if (splittedRow.isEmpty())
        throw IllegalArgumentException("Empty line in 'Route_description.")
    return OrderedCheckpointsRoute(
        splittedRow[0],
        splittedRow.subList(1, splittedRow.size)
    )
}

class OrderedCheckpointsRoute(
    name: String,
    private val route: List<CheckpointLabelT>
) : Route(name, route.toSet()) {
    override fun calculateResultingTime(
        checkpointsToTimes: List<CheckpointLabelAndTime>,
        startingTime: Time
    ): Time? {
        val checkpointsToTimesChronological =
            checkpointsToTimes.sortedBy { it.time }
        val chronologicalCheckpoints =
            checkpointsToTimesChronological.map { it.checkpointLabel }
        if (chronologicalCheckpoints != route) {
            Logger.warn {
                "Current participant passed checkpoints in wrong order (expected: ${
                    route
                }, actual: $chronologicalCheckpoints). Disqualifying."
            }
            return null
        } else {
            if (checkpointsToTimes.minOf { it.time } < startingTime) {
                Logger.warn {
                    "current participant passed his first checkpoint (at ${checkpointsToTimes.minOf { it.time }}) before he is supposed to start (${
                        startingTime
                    }). Disqualifying."
                }
                return null
            }
            val finishTime = checkpointsToTimesChronological.last().time
            val timeForDistance = finishTime - startingTime
            return Time(timeForDistance)
        }
    }
}

class AtLeastKCheckpointsRoute(
    name: String,
    checkpoints: Set<GroupLabelT>,
    val k: Int
) : Route(name, checkpoints) {
    init {
        require(k <= checkpoints.size) { "k must not be greater than the number of checkpoints." }
    }

    override fun calculateResultingTime(
        checkpointsToTimes: List<CheckpointLabelAndTime>,
        startingTime: Time
    ): Time? {
        val visitedCheckpointFromRoute = checkpointsToTimes
            .filter { it.checkpointLabel in checkpoints }
            .sortedBy { it.time }
        val lastRelevantCheckpoint = visitedCheckpointFromRoute
            .elementAtOrNull(k - 1) ?: return null
        return Time(lastRelevantCheckpoint.time - startingTime)
    }

}