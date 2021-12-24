package ru.emkn.kotlin.sms.io

import com.github.michaelbull.result.*
import ru.emkn.kotlin.sms.ResultOrMessage
import ru.emkn.kotlin.sms.csv.FileContent
import java.io.File

fun readFileContent(file: File): ResultOrMessage<FileContent> {
    if (!file.exists()) {
        return Err("File \"$file\" doesn't exist.")
    }
    if (!file.canRead()) {
        return Err("File \"$file\" cannot be read.")
    }
    return runCatching { file.readLines() }.mapError { it.message }
}
