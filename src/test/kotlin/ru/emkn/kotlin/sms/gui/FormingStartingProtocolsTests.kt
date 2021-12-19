package ru.emkn.kotlin.sms.gui

import ru.emkn.kotlin.sms.gui.frontend.launchGUI

internal class FormingStartingProtocolsTests {
    private val formingStartingProtocolsProgramState =
        TestDataSet1.formingStartingProtocolsProgramState
    //@Test
    fun `FormingStartingProtocols mode test`() {
        launchGUI(formingStartingProtocolsProgramState)
    }
}