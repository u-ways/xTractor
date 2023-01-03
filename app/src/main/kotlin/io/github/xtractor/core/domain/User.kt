package io.github.xtractor.core.domain

import java.time.LocalDate

data class User (
    val name: String,
    val joined: LocalDate,
    val logs: List<Log>,
)
