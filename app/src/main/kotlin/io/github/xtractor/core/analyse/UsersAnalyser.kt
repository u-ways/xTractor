package io.github.xtractor.core.analyse

import io.github.xtractor.core.analyse.LogsAnalyser.Companion.ACCEPT_ANY_LOG
import io.github.xtractor.core.analyse.UsersAnalyser.Companion.ContinuousUsersAnalyser
import io.github.xtractor.core.analyse.UsersAnalyser.Companion.DiscreteUsersAnalyser
import io.github.xtractor.core.domain.Log
import io.github.xtractor.core.domain.User
import java.util.function.Predicate

class UsersAnalyser(
    private val logs: LogsAnalyser = LogsAnalyser(),
    private val filter: Predicate<Log> = ACCEPT_ANY_LOG
) {
    internal val mostActiveMonth = DiscreteUsersAnalyser { users, predicate ->
        users
            .filter(predicate::test)
            .associateWith { logs.findMostActiveMonth.execute(it.logs, filter) }
    }

    internal val totalForMostActiveMonth = DiscreteUsersAnalyser { users, predicate ->
        users
            .filter(predicate::test)
            .associateWith { logs.countMostActiveMonth.execute(it.logs, filter) }
    }

    internal val totalNumberOfWorkoutsPerMonth = ContinuousUsersAnalyser { users, predicate ->
        users
            .filter(predicate::test)
            .associateWith { logs.countLogsPerMonth.execute(it.logs, filter) }
    }

    internal val totalNumberForAllWorkouts = DiscreteUsersAnalyser { users, predicate ->
        users
            .filter(predicate::test)
            .associateWith { logs.countAllLogs.execute(it.logs, filter) }
    }

    companion object {
        internal val ACCEPT_ANY_USER = Predicate<User> { true }

        fun interface ContinuousUsersAnalyser<NUM : Number> : Analyser<User, Map<User, List<NUM>>> {
            override fun execute(input: List<User>, predicate: Predicate<User>): Map<User, List<NUM>>
        }

        fun interface DiscreteUsersAnalyser<NUM : Number> : Analyser<User, Map<User, NUM>> {
            override fun execute(input: List<User>, predicate: Predicate<User>): Map<User, NUM>
        }
    }
}
