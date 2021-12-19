package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.time.Time

/**
 * Participants' final result (after competition has been finished).
 */
sealed class FinalParticipantResult : Comparable<FinalParticipantResult>,
    CsvStringDumpable {
    /**
     *  1. Participant has finished the route with [totalTime].
     *  He goes before all participants that are disqualified.
     *  Two finished participants are compared by [totalTime].
     */
    data class Finished(val totalTime: Time) : FinalParticipantResult() {
        override operator fun compareTo(other: FinalParticipantResult): Int =
            when (other) {
                is Finished -> totalTime.compareTo(other.totalTime)
                is Disqualified -> -1
            }

        override fun dumpToCsvString(): String = totalTime.toString()
    }

    /**
     * 2. Participant has been disqualified.
     * He goes after all non-disqualified participants.
     * Two disqualified participants are always equal.
     * TODO: differentiate Disqualified and Unfinished?
     */
    class Disqualified : FinalParticipantResult() {
        override operator fun compareTo(other: FinalParticipantResult): Int =
            when (other) {
                is Finished -> 1
                is Disqualified -> 0
            }

        override fun dumpToCsvString(): String = "снят"
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