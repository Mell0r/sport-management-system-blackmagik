package ru.emkn.kotlin.sms

import org.tinylog.kotlin.Logger

fun logErrorAndThrow(message: String): Nothing {
    Logger.error(message)
    throw IllegalArgumentException(message)
}