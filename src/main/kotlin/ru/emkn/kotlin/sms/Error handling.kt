package ru.emkn.kotlin.sms

import org.tinylog.kotlin.Logger

fun logErrorAndThrow(message: String): Nothing {
    Logger.error(message)
    throw IllegalArgumentException(message)
}

fun String.toIntOrThrow(e: Exception): Int {
    return toIntOrNull() ?: throw e
}
