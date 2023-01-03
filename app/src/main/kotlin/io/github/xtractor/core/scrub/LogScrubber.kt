package io.github.xtractor.core.scrub

import io.github.xtractor.core.domain.Log

object LogScrubber {
    internal val REMOVE_EMPTY_MESSAGES = Scrubber<Log> { log ->
        log.takeIf { it.message.isNotBlank() }
    }
}