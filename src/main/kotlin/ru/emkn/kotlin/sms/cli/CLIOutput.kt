package ru.emkn.kotlin.sms.cli

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.io.FileContent
import java.io.File

/**
 * Following two functions implement a (yes/no) answer console interface.
 */

fun getAYesNoAnswerOrNull(): Boolean? {
    return when (readLine()?.lowercase()) {
        "y", "yes", "1", "true" -> true
        "n", "no", "0", "false" -> false
        else -> null
    }
}

fun askYesNoQuestionInConsole(question: String): Boolean {
    var answer: Boolean? = null
    while (answer == null) {
        println("$question (Y/N)")
        answer = getAYesNoAnswerOrNull()
    }
    return answer
}


/**
 * Writes [content] to a file with [fileName] in [outputDirectory].
 *
 * If the file already exists, asks the user permission to overwrite it.
 * If it couldn't write to a file, or some exception occurred, terminates the program.
 */
fun safeWriteContentToFile(
    content: FileContent,
    outputDirectory: File,
    fileName: String
) {
    val fileToWrite = File(outputDirectory, fileName)
    val path = fileToWrite.absolutePath
    if (fileToWrite.exists()) {
        if (!askYesNoQuestionInConsole(
                question = "File \"$path\" already exists. Overwrite?"
            )
        ) {
            Logger.info { "Skipping writing to file \"$path\"." }
            return
        }
    }
    try {
        fileToWrite.writeText(content.joinToString("\n"))
    } catch (e: Exception) {
        Logger.error {
            "Couldn't write to file \"$path\". Following exception occurred:\n" +
                    "${e.message}"
        }
        exitWithInfoLog()
    }
}
