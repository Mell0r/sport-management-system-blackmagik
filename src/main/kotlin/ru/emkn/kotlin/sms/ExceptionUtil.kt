package ru.emkn.kotlin.sms

import com.github.michaelbull.result.*
import org.tinylog.kotlin.Logger

typealias ResultOrMessage<T> = Result<T, String?>
typealias UnitOrMessage = ResultOrMessage<Unit>

fun errAndLog(message: String) : Err<String> {
    Logger.error(message)
    return Err(message)
}

fun catchIllegalArgumentExceptionToString(throwable: Throwable): String? {
    return when (throwable) {
        is IllegalArgumentException -> throwable.message
        else -> throw throwable // propagate the exception if we cannot handle it here
    }
}

inline fun <V, E> Result<V, E>.successOrNull(
    action: (E) -> Unit,
): V? {
    return this.mapBoth (
        success = {it},
        failure = {
            action(it)
            null
        },
    )
}

inline fun <V, E> Result<V, E>.successOrNothing(
    nothingReturner: (E) -> Nothing,
): V {
    return this.mapBoth (
        success = {it},
        failure = { nothingReturner(it) },
    )
}
