package q4.mapsapp.repository.timer

import android.widget.Toast
import java.text.DateFormat
import java.time.Month
import java.time.Year
import java.util.*

class TimerRepositoryImpl(
): TimerRepository {

    private var countDownToPickerDate: Long? = null

    override fun getTimer(year: Int,
                          month: Int,
                          dayOfMonth: Int): Long? {
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val pickerDateString: String =
            DateFormat.getDateInstance(DateFormat.FULL).format(calendar.time)


        try {

            val now = Date()
            val currentDate: Long = now.time
            val pickerDate: Long = calendar.timeInMillis
            countDownToPickerDate = pickerDate - currentDate

        } catch (e: Exception) {
            e.printStackTrace()

        }
        return countDownToPickerDate
    }
}