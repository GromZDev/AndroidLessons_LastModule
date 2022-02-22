package q4.mapsapp.ui.lessons

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import q4.mapsapp.R
import q4.mapsapp.data.Lessons
import q4.mapsapp.databinding.FragmentLessonsBinding
import q4.mapsapp.viewModel.LessonsFragmentViewModel

class LessonsFragment : Fragment(R.layout.fragment_lessons) {

    companion object {
        fun newInstance() = LessonsFragment()
    }

    interface OnItemViewClickListener {
        fun onItemViewClick()
    }

    private var _binding: FragmentLessonsBinding? = null
    private val binding get() = _binding!!
    private val lessonsViewModel: LessonsFragmentViewModel by lazy {
        ViewModelProvider(this).get(LessonsFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lessonsViewModel.getLiveData().observe(viewLifecycleOwner, { renderData(it) })
        lessonsViewModel.getLessonsFromLocal()
    }

    private fun renderData(appState: LessonsFragmentAppState) {
        when (appState) {
            is LessonsFragmentAppState.Success -> {
                binding.includedLoadingLayout.loadingLayout.visibility = View.GONE
                binding.lessonsRv.visibility = View.VISIBLE

                setLessonsData(appState.lessonsData)
            }
            is LessonsFragmentAppState.Loading -> {
                binding.includedLoadingLayout.loadingLayout.visibility = View.VISIBLE
                binding.lessonsRv.visibility = View.GONE

            }
            is LessonsFragmentAppState.Error -> {
                binding.includedLoadingLayout.loadingLayout.visibility = View.GONE
                binding.lessonsRv.visibility = View.VISIBLE
            }
        }
    }

    private fun setLessonsData(lessons: List<Lessons>) {
        val allLessons: RecyclerView = binding.lessonsRv
        allLessons.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        val lessonsRecyclerAdapter = MainLessonsAdapter(object : OnItemViewClickListener {
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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}