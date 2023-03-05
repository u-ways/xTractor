package io.github.xtractor.domain.whatsapp.extractor

import io.github.xtractor.core.domain.ANNOUNCEMENT_ACTOR
import io.github.xtractor.core.domain.Log
import io.github.xtractor.core.domain.User
import io.github.xtractor.core.domain.UserBuilder.Companion.userBuilder
import io.github.xtractor.core.extract.Extractor
import io.github.xtractor.core.scrub.Scrubber
import io.github.xtractor.domain.whatsapp.WhatsAppMessageTokens.ANNOUNCEMENT_ADMIN_ADDED
import java.io.File
import java.time.LocalDate
import java.time.LocalTime

class WhatsAppUsersExtractorStrategy(
    private val logsExtractor: Extractor<Log>,
    private val scrubbers: List<Scrubber<User>>,
) : Extractor<User> {
    override fun execute(source: File): List<User> {
        return logsExtractor
            .execute(source)
            .groupBy(Log::sender)
            .let { senderMessages ->
                val usersJoinDate = senderMessages
                    .getOrElse(ANNOUNCEMENT_ACTOR) {
                        UNKNOWN_JOIN_DATE_FOR_USER
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

    companion object {
        val UNKNOWN_JOIN_DATE_FOR_USER = listOf(Log(LocalDate.MIN, LocalTime.MIN, ANNOUNCEMENT_ACTOR, "Added on an unknown date."))
    }
}