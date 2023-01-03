package io.github.xtractor.core.domain

import kotlin.text.RegexOption.IGNORE_CASE

internal val WORKOUT_TOKEN = Regex("""\bx\b""", IGNORE_CASE)