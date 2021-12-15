package ru.emkn.kotlin.sms.gui

interface ModelListener<T> {
    fun modelChanged(model: T)
}