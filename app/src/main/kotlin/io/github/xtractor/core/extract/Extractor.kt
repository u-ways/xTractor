package io.github.xtractor.core.extract

import java.io.File

/**
 * Extractor couples the Data Collection life cycle.
 *
 * It entails steps using the identified data source, scrapping it, and
 * transforming it into a usable format.
 */
interface Extractor<T> {
    fun execute(source: File): List<T>
}