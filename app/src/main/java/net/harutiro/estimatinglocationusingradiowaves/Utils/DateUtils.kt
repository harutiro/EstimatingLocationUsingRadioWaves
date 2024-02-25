package net.harutiro.estimatinglocationusingradiowaves.Utils

import android.icu.text.SimpleDateFormat
import java.util.*

class DateUtils {

    companion object{
        fun getNowDate(): String {
            val df = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss",Locale.JAPAN)
            val date = Date(System.currentTimeMillis())
            return df.format(date)
        }

        fun stringToDate(dateString: String): Long {
            val df = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.JAPAN)
            val date = df.parse(dateString)

            return date.time
        }

        fun getTimeStamp():Long {
            return System.currentTimeMillis()
        }
    }
}