package io.github.xtractor.core.analyse

import java.util.function.Predicate

interface Analyser<INPUT, OUTPUT> {
    fun execute(input: List<INPUT>, predicate: Predicate<INPUT>): OUTPUT
}