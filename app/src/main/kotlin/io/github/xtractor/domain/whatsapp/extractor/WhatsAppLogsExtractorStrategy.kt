package io.github.xtractor.domain.whatsapp.extractor

import io.github.xtractor.core.domain.ANNOUNCEMENT_ACTOR
import io.github.xtractor.core.domain.Log
import io.github.xtractor.core.domain.LogBuilder
import io.github.xtractor.core.extract.Extractor
import io.github.xtractor.core.scrub.Scrubber
import io.github.xtractor.domain.whatsapp.WhatsAppAdapters.hhmmToIsoAdapter
import io.github.xtractor.domain.whatsapp.WhatsAppAdapters.ymdToIsoAdapter
import io.github.xtractor.domain.whatsapp.WhatsAppLogTokens
import java.io.File
import java.time.LocalDate
import java.time.LocalTime

internal class WhatsAppLogsExtractorStrategy(
    private val scrubbers: List<Scrubber<Log>> = emptyList(),
) : Extractor<Log> {
    override fun execute(source: File): List<Log> {
        val logs = mutableListOf<Log>()
        val reader = source.bufferedReader()
        var line: String? = reader.readLine()

        while (line != null) {
            val whatsappLog = LogBuilder.whatsappLogBuilder()

            val date = WhatsAppLogTokens.LOG_DATE
                .matchEntire(line)
                .let(::checkNotNull)
                .groupValues.last()
            val time = WhatsAppLogTokens.LOG_TIME
                .matchEntire(line)
                .let(::checkNotNull)
                .groupValues.last()
            val sender = WhatsAppLogTokens.LOG_SENDER
                .matchEntire(line)
                ?.groupValues?.last()
                ?: ANNOUNCEMENT_ACTOR
            val message = WhatsAppLogTokens.LOG_MESSAGE
                .matchEntire(line)
                .let(::checkNotNull)
                .groupValues.last()

            whatsappLog
                .withDate(LocalDate.parse(ymdToIsoAdapter(date)))
                .withTime(LocalTime.parse(hhmmToIsoAdapter(time)))
                .withSender(sender)

            val messageBuilder = StringBuilder(message)

            // Assume that the message is not complete until we find a line that starts with a date
            var partialMessage = reader.readLine()

            // Keep reading until we find a line that starts with a date, or we reach the end of the file
            while (partialMessage != null && WhatsAppLogTokens.LOG_DATE.matches(partialMessage).not()) {
                // Append the partial message to the message builder
                messageBuilder.append(partialMessage)
                partialMessage = reader.readLine()
            }

            // If we have reached this point, then we have reached the end of the current message
            whatsappLog
                .withMessage(messageBuilder.toString())
                .build()
                // Scrub the log to improve data quality
                .let { log -> scrubbers.fold(log as Log?) { acc, scrubber -> acc?.let(scrubber::execute) } }
                // Add the cleaned log to the list of extracted logs
                ?.let(logs::add)

            // Update the current line to the last line we read, so we don't get out of sync
            line = partialMessage
        }

        return logs
    }
}
