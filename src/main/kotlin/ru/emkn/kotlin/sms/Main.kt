package ru.emkn.kotlin.sms

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.cli.*

fun main(args: Array<String>) {
    Logger.debug { "Program started." }

    runCommandLineInterface(args)

    Logger.debug { "Program successfully finished." }
}

