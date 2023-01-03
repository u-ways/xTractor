package io.github.xtractor.domain.whatsapp

import java.util.UUID

internal object WhatsAppLogTokens {
    internal val LOG_DATE = Regex("""^(?<date>\d{2}/\d{2}/\d{4}),.+""")
    internal val LOG_TIME = Regex("""^.+?,\s(?<time>\d{2}:\d{2})\s-.+""")
    internal val LOG_SENDER = Regex("""^.+?,.+?\s-\s(?<sender>.+?):.+""")
    internal val LOG_MESSAGE = Regex("""^.+?,.+?\s-\s(?:.+?:\s)?(?<message>.+)$""")

    internal const val LOG_MESSAGE_WITH_MEDIA_OMITTED = "<Media omitted>"
    internal val LOG_MESSAGE_WITH_MEDIA_OMITTED_REPLACEMENT = "x - rate limited media ${UUID.randomUUID()}"
    internal const val LOG_MESSAGE_WITH_NON_RATE_LIMITED_MEDIA_OMITTED_REPLACEMENT = "x - workout shoot/screenshot/selfie"
}

