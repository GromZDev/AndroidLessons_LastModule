package q4.mapsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import q4.mapsapp.databinding.FragmentLessonsBinding

class LessonsFragment : Fragment(R.layout.fragment_lessons) {

    companion object {
        fun newInstance() = LessonsFragment()
    }

    private var _binding: FragmentLessonsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}