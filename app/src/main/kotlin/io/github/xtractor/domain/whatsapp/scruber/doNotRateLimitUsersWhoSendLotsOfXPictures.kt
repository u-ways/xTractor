package io.github.xtractor.domain.whatsapp.scruber

import io.github.xtractor.core.domain.User
import io.github.xtractor.core.scrub.Scrubber
import io.github.xtractor.domain.whatsapp.WhatsAppLogTokens.LOG_MESSAGE_WITH_MEDIA_OMITTED_REPLACEMENT
import io.github.xtractor.domain.whatsapp.WhatsAppLogTokens.LOG_MESSAGE_WITH_NON_RATE_LIMITED_MEDIA_OMITTED_REPLACEMENT

// NOTE: Some users are special, as they will send multiple "<Media omitted>" messages in a row within a single day,
//       So we are tailoring our scrubbing to not rate limit those users.
internal val DO_NOT_RATE_LIMIT_USERS_WHO_SEND_LOTS_OF_X_PICTURES = { usernames: List<String> ->
    Scrubber<User> { user ->
        user.takeIf { usernames.contains(it.name) }
            ?.copy(
                logs = user.logs.map { log ->
                    log
                        .takeIf { it.message == LOG_MESSAGE_WITH_MEDIA_OMITTED_REPLACEMENT }
                        ?.copy(message = LOG_MESSAGE_WITH_NON_RATE_LIMITED_MEDIA_OMITTED_REPLACEMENT)
                        ?: log
                }
            )
            ?: user
    }
}