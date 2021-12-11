package ru.emkn.kotlin.sms.cli

import ru.emkn.kotlin.sms.Competition
import java.io.File

abstract class ProgramCommand {
    abstract fun execute(competition: Competition, outputDirectory: File)
}