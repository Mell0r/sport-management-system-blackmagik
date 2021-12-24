package ru.emkn.kotlin.sms.csv

import ru.emkn.kotlin.sms.Competition
import ru.emkn.kotlin.sms.io.readAndParseFile
import ru.emkn.kotlin.sms.results_processing.FileContent
import java.io.File

interface CreatableFromCsvAndCompetition<T> {
    /**
     * @throws [IllegalArgumentException] if the file had wrong format.
     */
    fun readFromCsvContentAndCompetition(
        fileContent: FileContent,
        competition: Competition,
    ): T

    /**
     * @throws [IllegalArgumentException] if it could not read from file,
     * or the file had invalid format.
     */
    fun readFromCsvAndCompetition(
        file: File,
        competition: Competition,
    ): T {
        return readAndParseFile(
            file = file,
            competition = competition,
            parser = ::readFromCsvContentAndCompetition,
        )
    }
}