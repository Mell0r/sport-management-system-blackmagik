package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.time.Time

/**
 * Participant's result at a concrete moment during competition.
 */
sealed class LiveParticipantResult : Comparable<LiveParticipantResult> {

    /**
     *  1. Participant has finished the route with [totalTime].
     *  He goes before all participants which are in the process, or disqualified.
     *  Two finished participants are compared by [totalTime].
     */
    data class Finished(val totalTime: Time) : LiveParticipantResult() {
        override operator fun compareTo(other: LiveParticipantResult): Int =
            when (other) {
                is Finished -> totalTime.compareTo(other.totalTime)
                is InProcess, is Disqualified-> -1
            }
    }

    /**
     * 2. Participant is in the process of route completion:
     * [completedCheckpoints] so far, with [lastCheckpointTime].
     * He goes before all disqualified participants.
     * Two participants in process are first compared by [completedCheckpoints], then by [lastCheckpointTime].
     */
    data class InProcess(val completedCheckpoints: Int, val lastCheckpointTime: Time) : LiveParticipantResult() {
        override operator fun compareTo(other: LiveParticipantResult): Int =
            when (other) {
                is Finished -> 1
                is InProcess -> if (completedCheckpoints != other.completedCheckpoints) {
                    - completedCheckpoints + other.completedCheckpoints
                } else {
                    lastCheckpointTime.compareTo(other.lastCheckpointTime)
                }
                is Disqualified -> -1
            }
    }

    /**
     * 3. Participant has been disqualified.
     * He goes after all non-disqualified participants.
     * Two disqualified participants are always equal.
     */
    class Disqualified: LiveParticipantResult() {
        override operator fun compareTo(other: LiveParticipantResult): Int =
            when (other) {
                is Finished, is InProcess -> 1
                is Disqualified -> 0
            }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

}