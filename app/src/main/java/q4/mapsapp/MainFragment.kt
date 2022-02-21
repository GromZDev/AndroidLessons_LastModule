package q4.mapsapp

import android.R
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import q4.mapsapp.data.Homework
import q4.mapsapp.data.Lessons
import q4.mapsapp.databinding.FragmentMainBinding
import q4.mapsapp.ui.lessons.LessonsFragment
import q4.mapsapp.ui.main.HomeworkAdapter
import q4.mapsapp.ui.main.MainFragmentAppState
import q4.mapsapp.ui.main.MainLessonsAdapter
import q4.mapsapp.viewModel.MainFragmentViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private var dateSetListener: OnDateSetListener? = null
    private val mainLessonsViewModel: MainFragmentViewModel by lazy {
        ViewModelProvider(this).get(MainFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainLessonsViewModel.getLiveData().observe(viewLifecycleOwner, { renderData(it) })
        mainLessonsViewModel.getLessonsFromLocal()

        binding.btnDatePicker.setOnClickListener {
            dateSetListener = setListener()

            val datePickerDialog = initDialog()
            datePickerDialog.show()

        }
    }

    private fun renderData(appState: MainFragmentAppState) {
        when (appState) {
            is MainFragmentAppState.Success -> {
                binding.includedLoadingLayout.loadingLayout.visibility = View.GONE
                binding.lessonsRv.visibility = View.VISIBLE
                binding.homeworksRv.visibility = View.VISIBLE
                binding.lessonsTextview.visibility = View.VISIBLE
                binding.homeworkTextview.visibility = View.VISIBLE
                setLessonsData(appState.lessonsData, appState.homeworkData)
            }
            is MainFragmentAppState.Loading -> {
                binding.includedLoadingLayout.loadingLayout.visibility = View.VISIBLE
                binding.lessonsRv.visibility = View.GONE
                binding.homeworksRv.visibility = View.GONE
                binding.lessonsTextview.visibility = View.GONE
                binding.homeworkTextview.visibility = View.GONE
            }
            is MainFragmentAppState.Error -> {
                binding.includedLoadingLayout.loadingLayout.visibility = View.GONE
                binding.lessonsRv.visibility = View.VISIBLE
                binding.homeworksRv.visibility = View.VISIBLE
                binding.lessonsTextview.visibility = View.VISIBLE
                binding.homeworkTextview.visibility = View.VISIBLE
            }
        }
    }

    private fun setLessonsData(lessons: List<Lessons>, homework: List<Homework>) {
        val allLessons: RecyclerView = binding.lessonsRv
        allLessons.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        val lessonsRecyclerAdapter = MainLessonsAdapter(object :
            LessonsFragment.OnItemViewClickListener {
            override fun onItemViewClick() {
                val intent: Intent? =
                    activity?.packageManager?.getLaunchIntentForPackage("com.whatsapp")
                if (intent != null) {
                    startActivity(intent)
                }
            }
        })
        allLessons.adapter = lessonsRecyclerAdapter
        lessonsRecyclerAdapter.setLessons(lessons)
        for (doc in lessons) {
            if (doc.time.toDouble() <= getCurrentTime().toDouble()) {
                allLessons.scrollToPosition(doc.id)
            }
            /** Проверка
            if (doc.time.toDouble() <= 11.35){
            allLessons.scrollToPosition(doc.id)
            }
             */
        }


        val allHomeworks: RecyclerView = binding.homeworksRv
        allHomeworks.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        val homeworkAdapter = HomeworkAdapter()
        allHomeworks.adapter = homeworkAdapter
        homeworkAdapter.setHomeworks(homework)

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
                Toast.makeText(context, "Таймер установлен!", Toast.LENGTH_SHORT).show()
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

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("HH.mm")
        return dateFormat.format(calendar.time)
    }

}