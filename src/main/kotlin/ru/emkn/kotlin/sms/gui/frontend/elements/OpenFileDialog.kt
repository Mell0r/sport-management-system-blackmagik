package ru.emkn.kotlin.sms.gui.frontend.elements

import androidx.compose.ui.awt.ComposeWindow
import java.awt.FileDialog
import java.io.File
import javax.swing.JFileChooser

fun openFileDialog(
    title: String,
    allowMultiSelection: Boolean = true
): Set<File> {
    return FileDialog(ComposeWindow(), title, FileDialog.LOAD).apply {
        isMultipleMode = allowMultiSelection
        isVisible = true
    }.files.toSet()
}

fun saveFileDialog(
    title: String,
    allowMultiSelection: Boolean = false
): Set<File> {
    return FileDialog(ComposeWindow(), title, FileDialog.SAVE).apply {
        isMultipleMode = allowMultiSelection
        isVisible = true
    }.files.toSet()
}

fun pickFolderDialog(): File? {
    val jFileChooser = JFileChooser()
    jFileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
    val returnVal = jFileChooser.showDialog(ComposeWindow(), "Select")
    return if (returnVal == JFileChooser.APPROVE_OPTION) {
        jFileChooser.selectedFile
    } else {
        null
    }
}

