package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.io.readAndParseFile
import ru.emkn.kotlin.sms.results_processing.FileContent
import java.io.File

interface CreatableFromFileContentAndCompetition<T> {
    /**
     * @throws [IllegalArgumentException] if the file had wrong format.
     */
    fun readFromFileContentAndCompetition(
        fileContent: FileContent,
        competition: Competition,
    ): T

    /**
     * @throws [IllegalArgumentException] if it could not read from file,
     * or the file had invalid format.
     */
    fun readFromFileAndCompetition(
        file: File,
        competition: Competition,
    ): T {
        return readAndParseFile(
            file = file,
            competition = competition,
            parser = ::readFromFileContentAndCompetition,
        )
    }
}