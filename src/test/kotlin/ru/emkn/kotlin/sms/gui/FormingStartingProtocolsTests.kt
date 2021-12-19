package ru.emkn.kotlin.sms.gui

import ru.emkn.kotlin.sms.gui.frontend.launchGUI

internal class FormingStartingProtocolsTests {
    private val formingStartingProtocolsProgramState =
        TestDataSet1.formingStartingProtocolsProgramState

    fun `FormingStartingProtocols mode test`() {
        launchGUI(formingStartingProtocolsProgramState)
    }
}

fun main() =
    FormingStartingProtocolsTests().`FormingStartingProtocols mode test`()