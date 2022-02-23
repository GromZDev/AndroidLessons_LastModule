package q4.mapsapp.ui.lessons

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import android.content.ActivityNotFoundException
import q4.mapsapp.ui.main.MainFragment


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
    private val isInstalled: Boolean = false

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

                initSkype(requireContext())

            }
        })


        allLessons.adapter = lessonsRecyclerAdapter
        lessonsRecyclerAdapter.setLessons(lessons)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initSkype(mContext: Context) {
        if (!isSkypeInstalled(mContext)) {
            goToMarket(mContext)
            return
        }

        val skype = Intent("android.intent.action.VIEW")
        skype.data = Uri.parse("skype:dmitryev")
        startActivity(skype)
        return

    }

    private fun isSkypeInstalled(mContext: Context): Boolean {
        val myPackageMgr: PackageManager = mContext.packageManager
        try {
            myPackageMgr.getPackageInfo("com.skype.raider", PackageManager.GET_ACTIVITIES)
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
        return true
    }

    private fun goToMarket(mContext: Context) {
        val marketUri = Uri.parse("market://details?id=com.skype.raider")
        val myIntent = Intent(Intent.ACTION_VIEW, marketUri)
        myIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        mContext.startActivity(myIntent)
        return
    }

}