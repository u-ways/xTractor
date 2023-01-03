package io.github.xtractor.core.domain

import java.time.LocalDate

class UserBuilder private constructor() {
    private lateinit var name: String
    private lateinit var joined: LocalDate
    private val logs = mutableListOf<Log>()

    fun name(name: String) = apply { this.name = name }
    fun joined(joined: LocalDate) = apply { this.joined = joined }
    fun logs(logs: List<Log>) = apply { this.logs.addAll(logs) }

    fun build() = User(name, joined, logs)

    companion object {
        fun userBuilder() = UserBuilder()
    }
}

