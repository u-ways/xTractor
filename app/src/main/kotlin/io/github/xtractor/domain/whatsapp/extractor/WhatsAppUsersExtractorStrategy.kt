package io.github.xtractor.domain.whatsapp.extractor

import io.github.xtractor.core.domain.ANNOUNCEMENT_ACTOR
import io.github.xtractor.core.domain.Log
import io.github.xtractor.core.domain.User
import io.github.xtractor.core.domain.UserBuilder.Companion.userBuilder
import io.github.xtractor.core.extract.Extractor
import io.github.xtractor.core.scrub.Scrubber
import io.github.xtractor.domain.whatsapp.WhatsAppMessageTokens.ANNOUNCEMENT_ADMIN_ADDED
import java.io.File

class WhatsAppUsersExtractorStrategy(
    private val logsExtractor: Extractor<Log>,
    private val scrubbers: List<Scrubber<User>>,
) : Extractor<User> {
    override fun execute(source: File) = logsExtractor
        .execute(source)
        .groupBy(Log::sender)
        .let { senderMessages ->
            val usersJoinDate = senderMessages
                .getOrElse(ANNOUNCEMENT_ACTOR) {
                    error("Cannot extract user's WhatsApp group join date without announcement logs...")
                }
                .mapNotNull { log ->
                    ANNOUNCEMENT_ADMIN_ADDED.matchEntire(log.message)
                        ?.groupValues
                        ?.last()
                        ?.let { username -> username to log.date }
                }
                .toMap()

            val earliestMessageDate = senderMessages
                .firstNotNullOf { it.value.minOfOrNull(Log::date) }

            senderMessages
                .minus(ANNOUNCEMENT_ACTOR)
                .map { (sender, logs) ->
                    userBuilder()
                        .name(sender)
                        .joined(usersJoinDate[sender] ?: earliestMessageDate)
                        .logs(logs)
                        .build()
                }
        }
        .mapNotNull { user ->
            scrubbers.fold(user as User?) { acc, scrubber -> acc?.let(scrubber::execute) }
        }
}