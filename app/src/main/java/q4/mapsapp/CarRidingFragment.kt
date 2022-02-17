package q4.mapsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import q4.mapsapp.databinding.FragmentCarRidingBinding

class CarRidingFragment : Fragment() {

    companion object {
        fun newInstance() = CarRidingFragment()
    }

    private var _binding: FragmentCarRidingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarRidingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}