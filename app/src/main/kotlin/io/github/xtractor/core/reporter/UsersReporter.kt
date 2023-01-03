package io.github.xtractor.core.reporter

import io.github.xtractor.Launcher.writeToFile
import io.github.xtractor.core.analyse.UsersAnalyser
import io.github.xtractor.core.analyse.UsersAnalyser.Companion.ACCEPT_ANY_USER
import io.github.xtractor.core.domain.Log
import io.github.xtractor.core.domain.User
import io.github.xtractor.core.extract.Extractor
import java.io.File
import java.time.Month
import java.util.function.Predicate

class UsersReporter(
    private val extractor: Extractor<User>,
    private val filter: Predicate<User> = ACCEPT_ANY_USER,
) : Reporter<User> {
    private val analyser: UsersAnalyser = UsersAnalyser()

    override fun execute(source: File): String {
        val users = extractor.execute(source)

        val mostActiveMonths = analyser
            .mostActiveMonth.execute(users, filter)

        val reportUsersOverview = users.sortedByDescending { it.logs.size }.joinToString("\n") {
            "  - ${it.name} (${it.joined}, ${it.logs.size}, ${Month.of(mostActiveMonths[it]!!).name})"
        }

        val reportMostActiveUserPerMonth = analyser
            .totalNumberOfWorkoutsPerMonth.execute(users, filter)
            .let { usersToMonthStats ->
                Month.values().mapNotNull { month ->
                    val peakUser = usersToMonthStats.maxBy { (_, stats) -> stats[month.value - 1] }.key
                    val peakCount = usersToMonthStats[peakUser]!![month.value - 1]
                    peakCount.takeIf { it > 0 }?.let { "  - $month: ${peakUser.name} ($peakCount)" }
                }
            }
            .joinToString("\n")

        users
            .sortedByDescending { it.logs.size }
            .joinToString("\n", "name,joined,logs_total,most_active_month\n") {
                "${it.name},${it.joined},${it.logs.size},${Month.of(mostActiveMonths[it]!!).name}"
            }
            .writeToFile("${source.nameWithoutExtension}-users-overview-data.csv")

        users
            .sortedBy { it.name }
            .joinToString("\n", "name,date,time,log\n") { user ->
                user.logs
                    .sortedByDescending(Log::date)
                    .joinToString("\n") { "${it.sender},${it.date},${it.time},\"${it.message}\"" }
            }
            .writeToFile("${source.nameWithoutExtension}-users-x-logs-data.csv")

        return """
            |### Stats
            |  - Total Xs sent: ${users.map(User::logs).map(List<Log>::size).reduce(Int::plus)}
            |  - Most active X month: ${Month.of(mostActiveMonths.values.max()).name}
            |  - X Grammy award goes to: ${users.maxBy { it.logs.size }.name}
            |
            |### Participants: (Join Date, Total Workouts, Most Active Month)
            |$reportUsersOverview
            |
            |### Most active X per month
            |$reportMostActiveUserPerMonth
        """.trimMargin()
    }
}