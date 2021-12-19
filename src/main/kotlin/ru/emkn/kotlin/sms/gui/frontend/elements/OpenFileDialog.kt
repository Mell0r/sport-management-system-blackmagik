package ru.emkn.kotlin.sms.gui.frontend.elements

import androidx.compose.ui.awt.ComposeWindow
import org.tinylog.kotlin.Logger
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
    val returnVal = jFileChooser.showSaveDialog(ComposeWindow())
    return if (returnVal == JFileChooser.APPROVE_OPTION) {
        jFileChooser.selectedFile
    } else {
        null
    }
}


fun safeOpenSingleFileOrNull(title: String): File? {
    val files = openFileDialog(title, false)
    if (files.size != 1) {
        Logger.error { "User did not select exactly one participants list file." }
        if (files.size > 1) {
            // User probably did something wrong, open failure window
            // TODO failure window
        }
        return null
    }
    return files.single()
}

