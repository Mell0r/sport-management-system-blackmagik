package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.results_processing.FileContent

/*

Class requirements:
o - means 'implements CsvDumpable'
i - means 'companion object of respected class T contains method
fromFileContent(fileContent: FileContent): T, that throws Illegal argument exception
if the format is bad.

i o classname

i   Application
i o ParticipantList
i o StartingProtocol
i   ParticipantTimestampsProtocol
i   CheckpointTimestampsProtocol
i o GroupResultProtocol*
  o TeamResultsProtocol*

* - these classes don't exist, although they should

 */
@Suppress("unused")
interface CsvDumpable {
    fun dumpToCsv(): FileContent
}

@Suppress("unused")
interface CreatableFromFileContent<T> {
    fun readFromFileContent(fileContent: FileContent): T
}