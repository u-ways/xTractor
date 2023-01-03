package io.github.xtractor.domain.whatsapp.scruber

import io.github.xtractor.core.domain.Log
import io.github.xtractor.core.scrub.Scrubber
import io.github.xtractor.domain.whatsapp.WhatsAppLogTokens.LOG_MESSAGE_WITH_MEDIA_OMITTED
import io.github.xtractor.domain.whatsapp.WhatsAppLogTokens.LOG_MESSAGE_WITH_MEDIA_OMITTED_REPLACEMENT

internal val REPLACE_MEDIA_OMITTED_WITH_AN_X = Scrubber<Log> { log ->
    log
        .message
        .replace(LOG_MESSAGE_WITH_MEDIA_OMITTED, LOG_MESSAGE_WITH_MEDIA_OMITTED_REPLACEMENT)
        .let { log.copy(message = it) }
}