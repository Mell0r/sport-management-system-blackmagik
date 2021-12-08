package ru.emkn.kotlin.sms

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.results_processing.CheckpointLabelAndTime
import ru.emkn.kotlin.sms.results_processing.CheckpointLabelT
import ru.emkn.kotlin.sms.time.Time

sealed class Route(val checkpoints: Set<CheckpointLabelT>) {
    // null if disqualified
    abstract fun calculateResultingTime(
        checkpointsToTimes: List<CheckpointLabelAndTime>,
        startingTime: Time
    ): Time?
}

class OrderedCheckpointsRoute(
    val name: String,
    private val route: List<CheckpointLabelT>
) : Route(route.toSet()) {
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