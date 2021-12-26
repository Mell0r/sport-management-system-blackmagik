package ru.emkn.kotlin.sms

enum class RouteType(val id: Int) {
    ORDERED_CHECKPOINTS(0),
    AT_LEAST_K_CHECKPOINTS(1),
}