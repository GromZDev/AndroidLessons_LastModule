package q4.mapsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding


abstract class BaseFragment<Binding : ViewBinding> : Fragment() {

    protected abstract val layoutId: Int

    protected var protectedBinding: Binding? = null
    protected val binding get() = protectedBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        protectedBinding = inflate(inflater, layoutId, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        protectedBinding = null
    }
}