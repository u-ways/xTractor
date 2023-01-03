package io.github.xtractor.core.scrub

/**
 * Scrubber couples the Data Cleaning/Preparation/Wrangling life cycle.
 *
 * It entails steps such as selecting relevant data, combining it by mixing data sets,
 * cleaning it, dealing with missing values by either removing them or imputing them with
 * relevant data, dealing with incorrect data by removing it, and also checking for and
 * dealing with outliers.
 */
fun interface Scrubber<T> {
    fun execute(logs: T): T?
}