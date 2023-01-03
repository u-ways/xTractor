package io.github.xtractor.domain.whatsapp.scruber

import io.github.xtractor.core.domain.User
import io.github.xtractor.core.scrub.Scrubber
import io.github.xtractor.domain.whatsapp.WhatsAppLogTokens.LOG_MESSAGE_WITH_MEDIA_OMITTED_REPLACEMENT

internal val RATE_LIMIT_MEDIA_OMITTED_POINTS = Scrubber<User> { user ->
    user.logs
        .groupBy { it.date }
        .mapValues { (_, sameDateLogs) ->
            sameDateLogs
                .filter { it.message == LOG_MESSAGE_WITH_MEDIA_OMITTED_REPLACEMENT }
                .take(1)
                .plus(sameDateLogs.filter { it.message != LOG_MESSAGE_WITH_MEDIA_OMITTED_REPLACEMENT })
        }
        .values
        .flatten()
        .let { user.copy(logs = it) }
}