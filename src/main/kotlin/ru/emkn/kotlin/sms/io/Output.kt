package ru.emkn.kotlin.sms.io

import java.io.File

fun writeContentToFile(file: File, content: List<String>) {
    file.writeText(content.joinToString("\n"))
}