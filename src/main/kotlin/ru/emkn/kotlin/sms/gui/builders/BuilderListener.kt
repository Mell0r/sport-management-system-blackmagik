package ru.emkn.kotlin.sms.gui.builders

interface BuilderListener<T> {
    fun dataChanged(model: T)
}