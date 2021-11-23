package ru.emkn.kotlin.sms.time

data class Time(
    val hours: Int,
    val minutes: Int,
    val seconds: Int
) {
    init {
        require(hours in 0..23) { "Hours must be in [0, 23]." }
        require(minutes in 0..59) { "Minutes must be in [0, 59]." }
        require(seconds in 0..59) { "Seconds must be in [0, 59]." }
    }

    constructor (seconds: Int) : this(
        seconds / 3600,
        (seconds % 3600) / 60,
        seconds % 60
    )

    companion object {
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
    }

    operator fun compareTo(other: Time): Int =
        this.asSeconds() - other.asSeconds()

    fun asSeconds(): Int = hours * 3600 + minutes * 60 + seconds
    operator fun minus(other: Time): Int = this.asSeconds() - other.asSeconds()

    override fun toString(): String {
        fun toTwoDigits(number: Int) = number.toString().padStart(2, '0')
        return listOf(
            toTwoDigits(hours),
            toTwoDigits(minutes),
            toTwoDigits(seconds)
        ).joinToString(":")
    }

}