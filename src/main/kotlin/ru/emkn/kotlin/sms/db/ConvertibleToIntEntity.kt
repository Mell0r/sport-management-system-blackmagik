package ru.emkn.kotlin.sms.db

import org.jetbrains.exposed.dao.Entity

interface ConvertibleToIntEntity<T : Entity<Int>> : ConvertibleToEntity<Int, T>
