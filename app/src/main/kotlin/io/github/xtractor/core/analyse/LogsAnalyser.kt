package io.github.xtractor.core.analyse

import io.github.xtractor.core.analyse.LogsAnalyser.Companion.ContinuousLogsAnalyser
import io.github.xtractor.core.analyse.LogsAnalyser.Companion.DiscreteLogsAnalyser
import io.github.xtractor.core.domain.Log
import java.util.function.Predicate

class LogsAnalyser {
    internal val findMostActiveMonth = DiscreteLogsAnalyser { logs, predicate ->
        logs.filter(predicate::test).groupBy { it.date.month }.maxBy { it.value.size }.key.value
    }

    internal val countMostActiveMonth = DiscreteLogsAnalyser { logs, predicate ->
        logs.filter(predicate::test).groupBy { it.date.month }.map { it.value.size }.maxOrNull() ?: 0
    }

    internal val countAllLogs = DiscreteLogsAnalyser { logs, predicate ->
        logs.count(predicate::test)
    }

    internal val countLogsPerMonth = ContinuousLogsAnalyser { logs, predicate ->
        logs.asSequence()
            .filter(predicate::test)
            .groupBy { it.date.month }
            .map { it.key to it.value.size }
            .fold(IntArray(12)) { acc, (month, count) ->
                acc.apply { this[month.value - 1] = count }
            }
            .toList()
    }

    companion object {
        internal val ACCEPT_ANY_LOG = Predicate<Log> { true }

        fun interface ContinuousLogsAnalyser<NUM : Number> : Analyser<Log, List<NUM>> {
            override fun execute(input: List<Log>, predicate: Predicate<Log>): List<NUM>
        }

        fun interface DiscreteLogsAnalyser<NUM : Number> : Analyser<Log, NUM> {
            override fun execute(input: List<Log>, predicate: Predicate<Log>): NUM
        }
    }
}
