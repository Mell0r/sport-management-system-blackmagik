package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.results_processing.FileContent

interface CreatableFromFileContentAndCompetition<T> {
    fun readFromFileContentAndCompetition(
        fileContent: FileContent,
        competition: Competition
    ): T
}