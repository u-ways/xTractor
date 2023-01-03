package io.github.xtractor.core.reporter

import io.github.xtractor.Launcher.writeToFile
import io.github.xtractor.core.analyse.LogsAnalyser
import io.github.xtractor.core.analyse.LogsAnalyser.Companion.ACCEPT_ANY_LOG
import io.github.xtractor.core.domain.Log
import io.github.xtractor.core.extract.Extractor
import java.io.File
import java.time.Month
import java.util.function.Predicate

class LogsReporter(
    private val extractor: Extractor<Log>,
    private val filter: Predicate<Log> = ACCEPT_ANY_LOG,
) : Reporter<Log> {
    private val analyser: LogsAnalyser = LogsAnalyser()

    override fun execute(source: File): String {
        val logs = extractor.execute(source)

        val reportAllLogs = analyser
            .countAllLogs
            .execute(logs, filter)

        val reportOverviewBySender = logs
            .groupBy { it.sender }
            .map { (sender, logs) -> "  - $sender (Total messages: ${logs.size})" }
            .sorted()
            .joinToString("\n")

        val reportMostActiveMonth = analyser
            .findMostActiveMonth.execute(logs, filter)
            .let { Month.of(it).name }

        val reportMostActiveSender = logs
            .groupBy { it.sender }
            .maxBy { it.value.size }
            .key

        logs
            .sortedBy { it.sender }
            .sortedBy { it.date }
            .joinToString("\n", "sender,date,time,log\n") {
                "${it.sender},${it.date},${it.time},\"${it.message}\""
            }
            .writeToFile("${source.nameWithoutExtension}-logs-data.csv")

        return """
            |### Stats
            |  - Total messages sent this year: $reportAllLogs
            |  - Most active month for this group: $reportMostActiveMonth
            |  - Spam award goes to: $reportMostActiveSender
            |
            |### Participants 
            |$reportOverviewBySender
        """.trimMargin()
    }
}