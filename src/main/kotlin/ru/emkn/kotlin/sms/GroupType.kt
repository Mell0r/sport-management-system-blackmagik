package ru.emkn.kotlin.sms

enum class GroupType(val textRepresentation: String) {
    AGE("age");
    companion object {
        val sqlType: String
            get() {
                val stringValues = values().joinToString(", ") {
                    "'${it.textRepresentation}'"
                }
                return "ENUM($stringValues)"
            }
    }
}