package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.dao.Entity

/**
 * Any object which can be converted
 * to [org.jetbrains.exposed] database entity of type [T] (a subclass of [Entity]).
 */
interface ConvertibleToEntity<ID : Comparable<ID>, T : Entity<ID>> {
    /**
     * Can be called ONLY within a transaction!
     */
    fun toEntity(): T
}