package io.github.xtractor.domain.whatsapp

internal object WhatsAppAdapters {
    internal fun ymdToIsoAdapter(ymdDate: String) = ymdDate
        .split("/")
        .let { (day, month, year) -> "$year-$month-$day" }

    internal fun hhmmToIsoAdapter(hhmmTime: String) = hhmmTime
        .let { if (it.length == 5) "$it:00" else it }
}