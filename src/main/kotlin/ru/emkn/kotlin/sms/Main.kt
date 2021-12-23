package ru.emkn.kotlin.sms

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.cli.runCommandLineInterface
import ru.emkn.kotlin.sms.gui.frontend.launchGUI

@Suppress("UNUSED_PARAMETER")
fun main(args: Array<String>) {
    Logger.debug { "Program started." }

    launchGUI()

    //runCommandLineInterface(args)
}

