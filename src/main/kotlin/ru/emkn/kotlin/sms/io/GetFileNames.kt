package ru.emkn.kotlin.sms.io

import ru.emkn.kotlin.sms.StartingProtocol

fun getFileName(protocol: StartingProtocol) = "Starting_protocol_of_'${protocol.group}'_group"