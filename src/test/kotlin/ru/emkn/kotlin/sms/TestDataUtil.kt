package ru.emkn.kotlin.sms

fun competitionToString(competition: Competition): String =
    "Competition(discipline='${competition.discipline}', " +
            "name='${competition.name}', " +
            "year=${competition.year}, " +
            "date='${competition.date}', " +
            "groups=${competition.groups}, " +
            "routes=${competition.routes})"

fun participantsListToString(participantsList: ParticipantsList): String =
    "PList(${participantsList.list})"

fun groupEquals(group1: Group, group2: Group): Boolean {
    // i couldn't make a consistent equals of Group (AgeGroup) due to inheritance
    if (group1.label != group2.label) return false
    if (group1.route != group2.route) return false
    if (group1 is AgeGroup && group2 !is AgeGroup) return false
    if (group1 !is AgeGroup && group2 is AgeGroup) return false
    if (group1 is AgeGroup && group2 is AgeGroup) {
        // AgeGroup case
        if (group1.ageFrom != group2.ageFrom) return false
        if (group1.ageTo != group2.ageTo) return false
        return true
    }
    return true
}

fun groupListsEquals(groupList1: List<Group>, groupList2: List<Group>): Boolean {
    fun contains(groupList: List<Group>, group: Group): Boolean {
        groupList.forEach {
            if (groupEquals(it, group)) {
                return true
            }
        }
        return false
    }
    fun containsAll(groupList1: List<Group>, groupList2: List<Group>): Boolean {
        groupList2.forEach { group ->
            if (!contains(groupList1, group)) {
                return false
            }
        }
        return true
    }
    return containsAll(groupList1, groupList2) && containsAll(groupList2, groupList1)
}

fun competitionEquals(competition1: Competition, competition2: Competition) : Boolean {
    if (competition1.discipline != competition2.discipline) return false
    if (competition1.name != competition2.name) return false
    if (competition1.year != competition2.year) return false
    if (competition1.date != competition2.date) return false
    if (competition1.groups.size != competition2.groups.size) return false
    for (i in competition1.groups.indices) {
        if (!groupEquals(competition1.groups[i], competition2.groups[i])) return false
    }
    if (competition1.routes != competition2.routes) return false
    return true
}
