package ru.emkn.kotlin.sms.time

/**
 * Arithmetic is modulo a single day.
 */
class Time(seconds: Int) : Comparable<Time> {

    private val totalSeconds: Int = modulo(seconds)

    val seconds: Int
        get() = totalSeconds % SECONDS_IN_MINUTE
    val minutes: Int
        get() = (totalSeconds / SECONDS_IN_MINUTE) % MINUTES_IN_HOUR
    val hours: Int
        get() = totalSeconds / SECONDS_IN_HOUR

    init {
        assert(totalSeconds in 0 until SECONDS_IN_DAY)
    }

    constructor(
        hours: Int,
        minutes: Int,
        seconds: Int
    ) : this(hours * SECONDS_IN_HOUR + minutes * SECONDS_IN_MINUTE + seconds) {
        require(hours in 0 until HOURS_IN_DAY)
        require(minutes in 0 until MINUTES_IN_HOUR)
        require(seconds in 0 until SECONDS_IN_MINUTE)
    }

    companion object {
        const val SECONDS_IN_MINUTE = 60
        const val MINUTES_IN_HOUR = 60
        const val HOURS_IN_DAY = 24

        const val SECONDS_IN_HOUR = SECONDS_IN_MINUTE * MINUTES_IN_HOUR
        const val SECONDS_IN_DAY = SECONDS_IN_HOUR * HOURS_IN_DAY

        /**
         * Parses the time string in format "HH:MM:SS" into the native time
         * class. Throws illegal argument exception if the format does not hold.
         */
        fun fromString(timeRepresentation: String): Time {
            val tokens = timeRepresentation.split(":")
            require(tokens.size == 3) { "There should be exactly two colons in the time string." }
            try {
                val (hours, minutes, seconds) = tokens.map { it.toInt() }
                return Time(hours, minutes, seconds)
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("One of the fields is not a number.")
            }
        }

        fun fromStringOrNull(timeRepresentation: String): Time? {
            return try {
                fromString(timeRepresentation)
            } catch (e: Throwable) {
                return null
            }
        }
    }

    private fun modulo(x: Int): Int {
        val rem = x % SECONDS_IN_DAY
        return when {
            rem >= 0 -> rem
            else -> SECONDS_IN_DAY - rem
        }
    }

    override operator fun compareTo(other: Time): Int =
        this.asSeconds() - other.asSeconds()

    fun asSeconds(): Int = totalSeconds

    operator fun plus(other: Time): Time = Time(totalSeconds + other.totalSeconds)
    operator fun minus(other: Time): Time = Time(totalSeconds - other.totalSeconds)
    operator fun times(scalar: Int): Time = Time(totalSeconds * scalar)

    override fun toString(): String {
        fun toTwoDigits(number: Int) = number.toString().padStart(2, '0')
        return listOf(
            toTwoDigits(hours),
            toTwoDigits(minutes),
            toTwoDigits(seconds)
        ).joinToString(":")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Time

        if (totalSeconds != other.totalSeconds) return false

        return true
    }

    override fun hashCode(): Int {
        return totalSeconds
    }

}

fun Int.s() = Time(this)