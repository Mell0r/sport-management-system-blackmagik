package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.IdTable

/*
 * Base class for entities with string id
 */
abstract class StringEntityClass<out E: Entity<String>>(
    table: IdTable<String>,
    entityType: Class<E>? = null
) : EntityClass<String, E>(table, entityType)
