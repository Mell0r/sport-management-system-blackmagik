package ru.emkn.kotlin.sms

enum class RouteProtocolType {
    OF_PARTICIPANT,
    OF_CHECKPOINT;
    companion object {
        val DEFAULT = OF_PARTICIPANT
    }
}