package ru.emkn.kotlin.sms

import com.github.michaelbull.result.*
import org.tinylog.kotlin.Logger
import java.io.IOException

typealias ResultOrMessage<T> = Result<T, String?>
typealias UnitOrMessage = ResultOrMessage<Unit>

fun errAndLog(message: String) : Err<String> {
    Logger.error(message)
    return Err(message)
}

inline fun <T> runIoOperation(f: () -> T): ResultOrMessage<T> =
    runCatching(f).mapError {
        when (it) {
            is IOException -> it.message
            else -> throw it
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
