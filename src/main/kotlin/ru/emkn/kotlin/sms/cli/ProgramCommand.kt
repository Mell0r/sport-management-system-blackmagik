package ru.emkn.kotlin.sms.cli

import ru.emkn.kotlin.sms.Competition
import java.io.File

interface ProgramCommand {
    fun execute(competition: Competition, outputDirectory: File)
}