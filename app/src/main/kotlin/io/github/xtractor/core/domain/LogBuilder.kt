package io.github.xtractor.core.domain

import java.time.LocalDate
import java.time.LocalTime

class LogBuilder private constructor() {
    private lateinit var date: LocalDate
    private lateinit var time: LocalTime
    private lateinit var sender: String
    private lateinit var message: String

    fun withDate(date: LocalDate) = apply { this.date = date }
    fun withTime(time: LocalTime) = apply { this.time = time }
    fun withSender(sender: String) = apply { this.sender = sender }
    fun withMessage(message: String) = apply { this.message = message }

    fun build() = Log(date, time, sender, message)

    companion object {
        fun whatsappLogBuilder() = LogBuilder()
    }
}