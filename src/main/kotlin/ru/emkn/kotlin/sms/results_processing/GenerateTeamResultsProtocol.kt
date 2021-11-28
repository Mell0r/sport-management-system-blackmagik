package ru.emkn.kotlin.sms.results_processing

import ru.emkn.kotlin.sms.GroupResultProtocol
import ru.emkn.kotlin.sms.ParticipantsList
import ru.emkn.kotlin.sms.TeamResultsProtocol
import ru.emkn.kotlin.sms.TeamToScore
import kotlin.math.roundToInt

fun generateTeamResultsProtocol(
    groupResultProtocols: List<GroupResultProtocol>,
    participantsList: ParticipantsList
): TeamResultsProtocol {
    val idToScore = HashMap<Int, Int>()
    groupResultProtocols.forEach { groupResultProtocol ->
        moveCalculatedScoresIntoMap(groupResultProtocol, idToScore)
    }
    val teamsToScore = groupResultProtocols
        .flatMap { it.entries }
        .groupBy { participantsList.getParticipantById(it.id)!!.team }
        .mapValues { (_, listParticipantAndTime) ->
            val idsOfTeam = listParticipantAndTime.map { it.id }
            idsOfTeam.sumOf { idToScore[it]!! }
        }
    return TeamResultsProtocol(teamsToScore.entries.map { (team, score) ->
        TeamToScore(
            team,
            score
        )
    })
}

private fun moveCalculatedScoresIntoMap(
    groupResultProtocol: GroupResultProtocol,
    idToScore: HashMap<Int, Int>
) {
    val bestResult: Int? =
        groupResultProtocol.entries.mapNotNull { it.totalTime }
            .minOfOrNull { it.asSeconds() }
    if (bestResult == null) {
        groupResultProtocol.entries.map { it.id }
            .forEach { id -> idToScore[id] = 0 }
    } else {
        groupResultProtocol.entries.map { it.id to it.totalTime?.asSeconds() }
            .forEach { (id, totalTime) ->
                val score = when (totalTime) {
                    null -> 0
                    else -> (100 * (2 - totalTime.toFloat() / bestResult.toFloat())).roundToInt()
                }
                idToScore[id] = score
            }
    }
}
