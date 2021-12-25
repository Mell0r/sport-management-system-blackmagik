package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.dao.IntEntity

/**
 * Any object which can be converted
 * to [org.jetbrains.exposed] database entity of type [T].
 */
interface ConvertibleToDBEntity<T : IntEntity> {
    /**
     * Actual extension function
     * that initializes all the values in the entity.
     */
    fun T.initializeEntity()
}