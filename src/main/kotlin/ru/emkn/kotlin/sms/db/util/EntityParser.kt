package ru.emkn.kotlin.sms.db.util

import org.jetbrains.exposed.dao.Entity
import ru.emkn.kotlin.sms.ResultOrMessage

/**
 * An object which can parse an object of type [V]
 * from a [Entity] of type [E].
 */
interface EntityParser<ID : Comparable<ID>, E : Entity<ID>, V> {
    fun parse(entity: E): ResultOrMessage<V>
}