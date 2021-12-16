package ru.emkn.kotlin.sms

fun competitionToString(competition: Competition) : String {
    return "Competition(discipline='${competition.discipline}', name='${competition.name}', year=${competition.year}, date='${competition.date}', groups=${competition.groups}, routes=${competition.routes})"
}

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