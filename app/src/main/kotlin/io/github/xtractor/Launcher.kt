package io.github.xtractor

import io.github.xtractor.core.domain.Log
import io.github.xtractor.core.domain.User
import io.github.xtractor.core.reporter.LogsReporter
import io.github.xtractor.core.reporter.UsersReporter
import io.github.xtractor.core.scrub.LogScrubber.REMOVE_EMPTY_MESSAGES
import io.github.xtractor.core.scrub.Scrubber
import io.github.xtractor.core.scrub.UserScrubber.REMOVE_LOG_MESSAGES_WITHOUT_AN_X
import io.github.xtractor.core.scrub.UserScrubber.REMOVE_USERS_WITHOUT_AN_X
import io.github.xtractor.domain.whatsapp.extractor.WhatsAppLogsExtractorStrategy
import io.github.xtractor.domain.whatsapp.extractor.WhatsAppUsersExtractorStrategy
import io.github.xtractor.domain.whatsapp.scruber.DAILY_RATE_LIMITER_CAP
import io.github.xtractor.domain.whatsapp.scruber.REPLACE_MEDIA_OMITTED_WITH_AN_X
import java.io.File

/** The main entry point of the application. */
object Launcher {
    private val mediaPowerUsers = System
        .getProperty("MediaPowerUsers")
        ?.split(",")
        ?: emptyList()

    // Log scrubbers applied based on order
    private val WORKOUT_LOG_SCRUBBERS: List<Scrubber<Log>> = listOf(
        REMOVE_EMPTY_MESSAGES,
        REPLACE_MEDIA_OMITTED_WITH_AN_X,
    )

    // User scrubbers applied based on order
    private val WORKOUT_USER_SCRUBBERS: List<Scrubber<User>> = listOf(
        REMOVE_LOG_MESSAGES_WITHOUT_AN_X,
        REMOVE_USERS_WITHOUT_AN_X,
        DAILY_RATE_LIMITER_CAP,
    )

    private val LOGS_REPORTER = LogsReporter(
        WhatsAppLogsExtractorStrategy()
    )

    private val USERS_REPORTER = UsersReporter(
        WhatsAppUsersExtractorStrategy(
            WhatsAppLogsExtractorStrategy(WORKOUT_LOG_SCRUBBERS),
            WORKOUT_USER_SCRUBBERS,
        )
    )

    @JvmStatic
    fun main(args: Array<String>) = args
        .firstOrNull()
        .let { checkNotNull(it) { "No log file argument provided" } }
        .let(::File)
        .apply { require(exists()) { "Log file does not exist: $absolutePath" } }
        .let { file ->
            """
            |Calculating for: ${file.name}
            |============================================================
            |
            |Group report
            |------------------------------------------------------------
            |
            |${LOGS_REPORTER.execute(file)}
            |
            |X report                                           
            |------------------------------------------------------------
            |
            |${USERS_REPORTER.execute(file)}
            |"""
            .trimMargin()
            .apply(::println)
            .writeToFile("${file.nameWithoutExtension}-report.md")
        }

    internal fun String.writeToFile(name: String) = Launcher::class.java.classLoader
        ?.let { "./app/build/reports/$name" }
        ?.let(::File)
        ?.apply { parentFile.mkdirs() }
        .let(::checkNotNull)
        .writeText(this)
}
