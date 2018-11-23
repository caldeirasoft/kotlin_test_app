package com.caldeirasoft.basicapp.util

import android.annotation.SuppressLint
import android.os.Binder
import android.os.Bundle
import androidx.core.app.BundleCompat
import androidx.fragment.app.Fragment
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Eases the Fragment.newInstance ceremony by marking the fragment's args with this delegate
 * Just write the property in newInstance and read it like any other property after the fragment has been created
 *
 * Inspired by Adam Powell, he mentioned it during his IO/17 talk about Kotlin
 */
class DateUtility {

    companion object {
        @SuppressLint("SimpleDateFormat")
        fun formatDateToTime(dateString: String): String {
            val formatToTime = SimpleDateFormat("HH:mm")
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("GMT")
            var date = sdf.parse(dateString)
            return formatToTime.format(date)
        }

        @SuppressLint("SimpleDateFormat")
        fun formatDateToDayAndMonth(date: Date): String {
            val formatToTime = SimpleDateFormat("MMMM dd")
            formatToTime.timeZone = TimeZone.getTimeZone("GMT")
            return formatToTime.format(date)
        }

        @SuppressLint("SimpleDateFormat")
        fun formatDateToDay(dateString: String): String {
            val formatToTime = SimpleDateFormat("yyyy-MM-dd")
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("GMT")
            val date = sdf.parse(dateString)
            return formatToTime.format(date)
        }

        fun sortFollowDate(list: ArrayList<String>) {
            Collections.sort<String>(list, Comparator { object1, object2 ->
                if (getDateValue(object1) == getDateValue(object2))
                    return@Comparator 0
                if (getDateValue(object1) < getDateValue(object2)) -1 else 1
            })
        }


        fun formatDateToDayOfWeek(date: Date): String {
            return SimpleDateFormat("EE" + "\n" + "dd").format(date)
        }

        fun epoch2Date(epochSeconds: Long): Date {
            val date = Date(epochSeconds)
            return date
        }

        private fun getDateValue(dateString: String?): Long {
            var ret = 0L
            if (dateString == null || dateString.isEmpty())
                return ret

            val sdf = SimpleDateFormat("yyyy-MM-dd")
            var date: Date? = null
            try {
                date = sdf.parse(dateString)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            if (date != null)
                ret = date.time
            return ret
        }
    }
}