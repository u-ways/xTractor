package io.github.xtractor.core.reporter

import java.io.File

interface Reporter<T> {
    fun execute(source: File): String
}