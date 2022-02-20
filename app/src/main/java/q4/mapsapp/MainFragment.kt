package q4.mapsapp

import android.R
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import q4.mapsapp.databinding.FragmentMainBinding
import java.text.DateFormat
import java.util.*


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private var dateSetListener: OnDateSetListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnDatePicker.setOnClickListener {

            dateSetListener = setListener()

            val datePickerDialog = initDialog()
            datePickerDialog.show()
        }
    }

    private fun setListener() =
        OnDateSetListener { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val pickerDateString: String =
                DateFormat.getDateInstance(DateFormat.FULL).format(calendar.time)

            val tvDatePicker = binding.datePickerTw
            val myCountdownView = binding.countdown
            try {
                Toast.makeText(context, "Пробую!", Toast.LENGTH_SHORT).show()
                tvDatePicker.text = pickerDateString
                val now = Date()
                val currentDate: Long = now.time
                val pickerDate: Long = calendar.timeInMillis
                val countDownToPickerDate = pickerDate - currentDate
                myCountdownView.start(countDownToPickerDate)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Ошибка!!!!", Toast.LENGTH_SHORT).show()
            }
        }

    private fun initDialog(): DatePickerDialog {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        return DatePickerDialog(
            requireContext(),
            R.style.Theme_Material_Dialog, dateSetListener,
            year, month, day
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}