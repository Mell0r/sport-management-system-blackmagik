package ru.emkn.kotlin.sms.db.util

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

/*
 * Base class for table objects with string id
 */
abstract class StringIdTable(
    name: String = "",
    idColumnName: String = "id",
    maxIdLength: Int,
) : IdTable<String>(name) {
    override val id: Column<EntityID<String>> = varchar(idColumnName, maxIdLength).entityId()
    override val primaryKey by lazy { super.primaryKey ?: PrimaryKey(id) }
}