package io.github.xtractor.core.scrub

import io.github.xtractor.core.domain.User
import io.github.xtractor.core.domain.WORKOUT_TOKEN

object UserScrubber {
    internal val REMOVE_LOG_MESSAGES_WITHOUT_AN_X = Scrubber<User> { user ->
        user.logs
            .filter { it.message.contains(WORKOUT_TOKEN) }
            .let { user.copy(logs = it) }
    }

    internal val REMOVE_USERS_WITHOUT_AN_X = Scrubber<User> { user ->
        user.takeIf { it.logs.any { log -> log.message.contains(WORKOUT_TOKEN) } }
    }

    internal val REMOVE_INACTIVE_USER = Scrubber<User> { user ->
        user.takeIf { it.logs.size > 10 }
    }
}