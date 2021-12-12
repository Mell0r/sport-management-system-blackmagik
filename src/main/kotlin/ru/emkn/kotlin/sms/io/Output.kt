package ru.emkn.kotlin.sms.io

import org.tinylog.kotlin.Logger
import ru.emkn.kotlin.sms.GroupResultProtocol
import ru.emkn.kotlin.sms.ParticipantsList
import ru.emkn.kotlin.sms.StartingProtocol
import ru.emkn.kotlin.sms.TeamResultsProtocol
import ru.emkn.kotlin.sms.cli.exitWithInfoLog
import ru.emkn.kotlin.sms.results_processing.FileContent
import java.io.File

/**
 * getFileNameOf* functions
 *
 * They generate a somewhat pretty output file names
 * based on data to dump.
 */
fun getFileNameOfParticipantsList(participantsList: ParticipantsList): String =
    "participants-list.csv"

fun getFileNameOfStartingProtocol(startingProtocol: StartingProtocol): String =
    "starting-protocol-of-group-${startingProtocol.group}.csv"

fun getFileNameOfGroupResultProtocol(groupResultProtocol: GroupResultProtocol): String =
    "result-of-group-${groupResultProtocol.group.label}.csv"

fun getFileNameOfTeamResultsProtocol(teamResultsProtocol: TeamResultsProtocol): String =
    "results-teams.csv"


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
