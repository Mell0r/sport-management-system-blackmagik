package ru.emkn.kotlin.sms

import com.github.michaelbull.result.*
import org.tinylog.kotlin.Logger

typealias ResultOrMessage<T> = Result<T, String?>
typealias UnitOrMessage = ResultOrMessage<Unit>

fun logErrorAndThrow(message: String): Nothing {
    Logger.error(message)
    throw IllegalArgumentException(message)
}

fun String.toIntOrThrow(e: Exception): Int = toIntOrNull() ?: throw e

fun catchIllegalArgumentExceptionToString(throwable: Throwable): String? {
    return when (throwable) {
        is IllegalArgumentException -> throwable.message
        else -> throw throwable // propagate the exception if we cannot handle it here
    }
}

inline fun <V, E> Result<V, E>.successOrNothing(
    nothingReturner: (E) -> Nothing,
): V {
    return this.mapBoth (
        success = {it},
        failure = { nothingReturner(it) },
    )
}