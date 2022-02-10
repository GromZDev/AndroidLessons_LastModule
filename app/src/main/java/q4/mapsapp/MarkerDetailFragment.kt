package q4.mapsapp

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import q4.mapsapp.databinding.FragmentMarkerDetailsBinding
import q4.mapsapp.model.Place
import java.util.*

class MarkerDetailFragment : Fragment() {

    private var receivedData: Place? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    companion object {
        const val BUNDLE_EXTRA = "MY_Detail_Marker"
        fun newInstance(bundle: Bundle?): MarkerDetailFragment {
            val fragment = MarkerDetailFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var _binding: FragmentMarkerDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarkerDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBottomSheetBehavior(binding.includedDetailsBottomSheetLayout.bottomSheetContainer)

        if (arguments !== null) {
            receivedData = arguments?.getParcelable(BUNDLE_EXTRA)
        }

        binding.markerTitleTextview.text = receivedData?.title
        binding.markerOverviewTextview.text = receivedData?.snippet

        val myAddress = getAddress()
        binding.markerLocTextview.text = myAddress

        binding.changeDataButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.includedDetailsBottomSheetLayout.titleEt.text =
                Editable.Factory.getInstance().newEditable(receivedData?.title.toString())

            binding.includedDetailsBottomSheetLayout.snippetEt.text =
                Editable.Factory.getInstance().newEditable(receivedData?.snippet.toString())

            binding.includedDetailsBottomSheetLayout.addButton.setOnClickListener {
                val title = binding.includedDetailsBottomSheetLayout.titleEt.text.toString().trim()
                val snippet = binding.includedDetailsBottomSheetLayout.snippetEt.text.toString().trim()
                if (snippet.isNotEmpty() && title.isNotEmpty()) {

                    val edited = Place(title, snippet, position = receivedData?.position)
                    val bundle = Bundle()
                    bundle.putParcelable(MyListFragment.BUNDLE_EXTRA_EDIT, edited)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, MyListFragment.newInstance(bundle))
                        .addToBackStack("")
                        .commitAllowingStateLoss()

                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    Toast.makeText(context, "Маркер успешно установлен!", Toast.LENGTH_SHORT).show()
                    binding.includedDetailsBottomSheetLayout.snippetEt.text?.clear()
                    binding.includedDetailsBottomSheetLayout.titleEt.text?.clear()
                } else if (snippet.isEmpty() || title.isEmpty()) {
                    Toast.makeText(context, "Что-то не заполнено!", Toast.LENGTH_SHORT).show()
                }

            }
        }

    }

    private fun getAddress(): String? {
        val location = LatLng(
            receivedData?.location?.latitude ?: 0.0,
            receivedData?.location?.longitude ?: 0.0
        )

        val geoCoder = Geocoder(context, Locale.getDefault())
        val myPlaceByLocation: List<Address> =
            geoCoder.getFromLocation(location.latitude, location.longitude, 1)
        return myPlaceByLocation[0].getAddressLine(0)
    }

    private fun setBottomSheetBehavior(bottomSheet: ConstraintLayout) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

}