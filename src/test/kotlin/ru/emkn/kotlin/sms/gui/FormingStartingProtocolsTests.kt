package ru.emkn.kotlin.sms.gui

import org.junit.Test
import ru.emkn.kotlin.sms.gui.frontend.launchGUI

internal class FormingStartingProtocolsTests {
    val formingStartingProtocolsProgramState = TestDataSet1.formingStartingProtocolsProgramState
    //@Test
    fun `FormingStartingProtocols mode test`() {
        launchGUI(formingStartingProtocolsProgramState)
    }
}