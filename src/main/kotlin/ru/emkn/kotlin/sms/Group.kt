package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.csv.CsvStringDumpable
import ru.emkn.kotlin.sms.db.ConvertibleToDBEntity
import ru.emkn.kotlin.sms.db.GroupEntity
import ru.emkn.kotlin.sms.startcfg.Applicant

abstract class Group(
    val label: String,
    val route: Route,
) : CsvStringDumpable, ConvertibleToDBEntity<GroupEntity> {

    abstract fun checkApplicantValidity(applicant: Applicant): Boolean

    override fun toString() = label
}