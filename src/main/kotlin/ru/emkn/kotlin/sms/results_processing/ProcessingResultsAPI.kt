package ru.emkn.kotlin.sms.results_processing

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.time.Time

typealias FileContent = List<String>

/*
This public functions are highly unlikely to change
and their signatures can be relied on.
 */
class FromStartProtocols(startProtocols: List<FileContent>) {

    val startProtocols = startProtocols.map { startProtocolFileContent ->
        val groupLabelAndHeaderRowLineCount = 2
        val tableString =
            startProtocolFileContent.drop(groupLabelAndHeaderRowLineCount)
                .joinToString("\n")
        val protocolEntries = csvReader().readAll(tableString).map { row ->
            StartingProtocolEntry(row[0].toInt(), Time.fromString(row[4]))
        }
        val groupLabel = startProtocolFileContent[0].split(",").first()
        StartingProtocol(protocolEntries, groupLabel)
    }

    private fun getStartingTimeById(id: Int): Time =
        startProtocols.flatMap { it.entries }.single { it.id == id }.startTime

    /**
     * Assumes that no completion protocols are missing.
     * @return Pairs groupName to FileContent with results of the respective group
     */
    fun generateResultFilesFromByParticipantsRouteCompletionProtocols(
        completionProtocols: List<FileContent>,
        idToParticipantMapping: (Int) -> Participant,
        competition: Competition
    ): Map<GroupLabelT, FileContent> {
        val processedProtocols =
            completionProtocols.map { completionProtocolFileContent ->
                readRouteCompletionByParticipantProtocol(
                    completionProtocolFileContent
                )
            }
        val results =
            getParticipantsTimesFromByParticipantProtocols(
                processedProtocols,
                { id ->
                    getRouteById(idToParticipantMapping, id, competition)
                },
                ::getStartingTimeById
            )
        return generateFullResultsFile(
            results
        ) { id -> idToParticipantMapping(id) }
    }


    fun generateResultFileFromByCheckpointRouteCompletionProtocols(
        completionProtocols: List<FileContent>,
        idToParticipantMapping: (Int) -> Participant,
        competition: Competition
    ): Map<GroupLabelT, FileContent> {
        val processedProtocols =
            completionProtocols.map { completionProtocolFileContent ->
                readRouteCompletionByCheckpointProtocol(
                    completionProtocolFileContent
                )
            }
        val results =
            getParticipantsTimesFromByCheckpointProtocols(
                processedProtocols,
                { id ->
                    getRouteById(idToParticipantMapping, id, competition)
                },
                ::getStartingTimeById
            )
        return generateFullResultsFile(
            results
        ) { id -> idToParticipantMapping(id) }
    }

    private fun getRouteById(
        idToParticipantMapping: (Int) -> Participant,
        id: Int,
        competition: Competition
    ): Route {
        val group = idToParticipantMapping(
            id
        ).supposedGroup
        val route = competition.groupToRouteMapping[group]
        return route ?: logErrorAndThrow(
            "Group $group does not " +
                    "have a route mapped to it"
        )
    }
}