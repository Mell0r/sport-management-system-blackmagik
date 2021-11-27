package ru.emkn.kotlin.sms.cli

import kotlinx.cli.ArgType
import java.io.File

/**
 * A custom [ArgType] object for [kotlinx.cli],
 * which return java.io.File object, created from given string.
 */
object FileArgType : ArgType<File>(true) {
    override val description = "{ File }"

    override fun convert(value: kotlin.String, name: kotlin.String) = File(value)
}