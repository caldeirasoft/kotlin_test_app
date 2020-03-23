package com.caldeirasoft.basicapp.util

import android.content.Context
import com.caldeirasoft.basicapp.R
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit

/**
 * Eases the Fragment.newInstance ceremony by marking the fragment's args with this delegate
 * Just write the property in newInstance and read it like any other property after the fragment has been created
 *
 * Inspired by Adam Powell, he mentioned it during his IO/17 talk about Kotlin
 */
class RelativeTimestampGenerator() {

    abstract class RelativeTimestamp {
        abstract fun displayText(context: Context): String
    }

    private fun generateDateTimeReference(today: LocalDateTime, time: Instant): LocalDateTime {
        val dateTime = time.atZone(ZoneOffset.UTC).toLocalDateTime()

        val todayAtMidnight = today.truncatedTo(ChronoUnit.DAYS)
        val yesterdayAtMidnight = todayAtMidnight.minusDays(1)
        val dayMinus2AtMidnight = todayAtMidnight.minusDays(2)
        val dayMinus6AtMidnight = todayAtMidnight.minusDays(6)
        val firstDayOfLastWeek = todayAtMidnight.minusDays(todayAtMidnight.dayOfWeek.value.toLong() - 1).minusWeeks(1)
        val firstDayOfMonth = todayAtMidnight.minusDays(todayAtMidnight.dayOfMonth.toLong() - 1)

        return when {
            dateTime.isAfter(todayAtMidnight) -> todayAtMidnight
            dateTime.isAfter(yesterdayAtMidnight) -> yesterdayAtMidnight
            dateTime.isAfter(dayMinus6AtMidnight) -> dateTime.truncatedTo(ChronoUnit.DAYS)
            dateTime.isAfter(firstDayOfLastWeek) -> firstDayOfLastWeek
            dateTime.isAfter(firstDayOfMonth) -> firstDayOfMonth
            else -> dateTime.truncatedTo(ChronoUnit.DAYS).minusDays(dateTime.dayOfMonth.toLong() - 1)
        }
    }

    fun generate(time: Instant): RelativeTimestamp {
        val dateTime = time.atZone(ZoneOffset.UTC).toLocalDateTime()
        return generate(dateTime)
    }

    fun generate(dateTime: LocalDateTime): RelativeTimestamp {
        val today = LocalDateTime.now(ZoneOffset.UTC)

        val todayAtMidnight = today.truncatedTo(ChronoUnit.DAYS)
        val yesterdayAtMidnight = todayAtMidnight.minusDays(1)
        val dayMinus2AtMidnight = todayAtMidnight.minusDays(2)
        val dayMinus6AtMidnight = todayAtMidnight.minusDays(6)
        val firstDayOfLastWeek = todayAtMidnight.minusDays(todayAtMidnight.dayOfWeek.value.toLong() - 1).minusWeeks(1)
        val firstDayOfMonth = todayAtMidnight.minusDays(todayAtMidnight.dayOfMonth.toLong() - 1)

        return when {
            dateTime.equals(todayAtMidnight) -> Today()
            dateTime.equals(yesterdayAtMidnight) -> Yesterday()
            dateTime.equals(dayMinus6AtMidnight) || dateTime.isAfter(dayMinus6AtMidnight) -> DayOfWeek(dateTime)
            dateTime.equals(firstDayOfLastWeek) || dateTime.isAfter(firstDayOfLastWeek) -> LastWeek()
            dateTime.equals(firstDayOfMonth) || dateTime.isAfter(firstDayOfMonth) -> ThisMonth()
            else -> OlderThanThisMonth(dateTime)
        }
    }

    fun generateDateTime(time: Instant): LocalDateTime {
        val today = LocalDateTime.now(ZoneOffset.UTC)
        return generateDateTimeReference(today, time)
    }

    class Today : RelativeTimestamp() {
        override fun displayText(context: Context): String {
            return context.getString(R.string.timestamp_today)
        }
    }

    class Yesterday : RelativeTimestamp() {
        override fun displayText(context: Context): String {
            return context.getString(R.string.timestamp_yesterday)
        }
    }

    class DayOfWeek(private val localDateTime: LocalDateTime) : RelativeTimestamp() {
        override fun displayText(context: Context): String {
            return localDateTime.format(DateTimeFormatter.ofPattern("EEE"))
        }
    }

    class LastWeek : RelativeTimestamp() {
        override fun displayText(context: Context): String {
            return context.getString(R.string.timestamp_lastweek)
        }
    }

    class ThisMonth : RelativeTimestamp() {
        override fun displayText(context: Context): String {
            return context.getString(R.string.timestamp_thismonth)
        }
    }

    class OlderThanThisMonth(private val localDateTime: LocalDateTime) : RelativeTimestamp() {
        override fun displayText(context: Context): String {
            return localDateTime.format(DateTimeFormatter.ofPattern("MMM"))
        }
    }
}