package io.github.xtractor.domain.whatsapp.scruber

import io.github.xtractor.core.domain.Log
import io.github.xtractor.core.domain.User
import io.github.xtractor.core.domain.WORKOUT_TOKEN
import io.github.xtractor.core.scrub.Scrubber

internal val DAILY_RATE_LIMITER_CAP = Scrubber<User> { user ->
    user.logs
        .groupBy(Log::date)
        .mapValues { (_, sameDateLogs) ->
            sameDateLogs
                .firstOrNull { it.message.contains(WORKOUT_TOKEN) }
                ?.let(::listOf)
                ?: sameDateLogs
        }
        .values
        .flatten()
        .let { user.copy(logs = it) }
}