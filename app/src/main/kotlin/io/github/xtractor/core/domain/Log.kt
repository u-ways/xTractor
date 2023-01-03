package io.github.xtractor.core.domain

import java.time.LocalDate
import java.time.LocalTime

data class Log(
    val date: LocalDate,
    val time: LocalTime,
    val sender: String,
    val message: String,
)