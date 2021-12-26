package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.dao.Entity

interface ConvertibleToStringEntity<T : Entity<String>> : ConvertibleToEntity<String, T>
