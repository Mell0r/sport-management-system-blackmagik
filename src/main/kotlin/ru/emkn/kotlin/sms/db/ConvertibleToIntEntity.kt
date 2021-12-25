package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.dao.IntEntity

/**
 * Any object which can be converted
 * to [org.jetbrains.exposed] database entity of type [T] (a subclass of [IntEntity]).
 */
interface ConvertibleToIntEntity<T : IntEntity> {
    /**
     * Can be called ONLY within transaction!
     */
    fun toEntity(): T
}