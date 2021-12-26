package ru.emkn.kotlin.sms

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.gui.frontend.launchGUI

typealias CheckpointLabelT = String

@Suppress("UNUSED_PARAMETER")
fun main(args: Array<String>) {
    Logger.debug { "Program started." }

    launchGUI()

    //runCommandLineInterface(args)
}

