package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.csv.CsvStringDumpable
import ru.emkn.kotlin.sms.db.util.RecordableToTableRow
import ru.emkn.kotlin.sms.db.schema.GroupsTable
import ru.emkn.kotlin.sms.startcfg.Applicant

abstract class Group(
    val label: String,
    val route: Route,
) : CsvStringDumpable, RecordableToTableRow<GroupsTable> {

    abstract fun checkApplicantValidity(applicant: Applicant): Boolean

    override fun toString() = label
}