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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.iwgang.countdownview.CountdownView
import q4.mapsapp.data.Homework
import q4.mapsapp.data.Lessons
import q4.mapsapp.databinding.FragmentMainBinding
import q4.mapsapp.ui.lessons.LessonsFragment
import q4.mapsapp.ui.main.HomeworkAdapter
import q4.mapsapp.ui.main.MainFragmentAppState
import q4.mapsapp.ui.main.MainLessonsAdapter
import q4.mapsapp.ui.main.TimerAppState
import q4.mapsapp.viewModel.MainFragmentViewModel
import q4.mapsapp.viewModel.TimerViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment() {

    companion object {
        const val COUNT_KEY = "COUNT_KEY"
        fun newInstance() = MainFragment()
    }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private var dateSetListener: OnDateSetListener? = null
    private val mainLessonsViewModel: MainFragmentViewModel by lazy {
        ViewModelProvider(this).get(MainFragmentViewModel::class.java)
    }
    private val timerViewModel: TimerViewModel by lazy {
        ViewModelProvider(this).get(TimerViewModel::class.java)
    }
    private lateinit var myCountdownView: CountdownView
    private var timeElapsed: Long = 0

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
        if (savedInstanceState == null) {
            timerViewModel.getLiveData().observe(viewLifecycleOwner, { timerData(it) })
        }

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

    private fun timerData(appState: TimerAppState) {
        when (appState) {
            is TimerAppState.Success -> {
                binding.includedLoadingLayout.loadingLayout.visibility = View.GONE
                setTimerData(appState.timerData)
            }
            is TimerAppState.Loading -> {
                binding.includedLoadingLayout.loadingLayout.visibility = View.VISIBLE

            }
            is TimerAppState.Error -> {
                binding.includedLoadingLayout.loadingLayout.visibility = View.GONE

            }
        }
    }

    private fun setTimerData(timer: Long) {
        val now = Date()
        val currentDate: Long = now.time
        val pickerDateString: String =
            DateFormat.getDateInstance(DateFormat.FULL).format(timer + currentDate)

        val tvDatePicker = binding.datePickerTw
        tvDatePicker.text = pickerDateString
        myCountdownView = binding.countdown

        myCountdownView.start(timer)
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
            timerViewModel.getTimerData(year, month, dayOfMonth)
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

    override fun onSaveInstanceState(outState: Bundle) { // Here You have to save count value
        super.onSaveInstanceState(outState)
        timeElapsed = myCountdownView.remainTime
        outState.putLong(COUNT_KEY, timeElapsed)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState !== null) {
            timeElapsed = savedInstanceState.getLong(COUNT_KEY)
            myCountdownView = binding.countdown
            myCountdownView.start(timeElapsed)
        }
    }
}