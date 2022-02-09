package q4.mapsapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import q4.mapsapp.databinding.FragmentMainMapsBinding

class MainMapsFragment : Fragment() {

    companion object {
        var PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        fun newInstance() = MainMapsFragment()
    }

    private var _binding: FragmentMainMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var thisMap: GoogleMap
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var markersList: MutableList<Marker> = mutableListOf()
    private var mapFragment: SupportMapFragment? = null

    private val callback = OnMapReadyCallback { googleMap ->
        thisMap = googleMap
        activateMyLocation(thisMap)
        thisMap.uiSettings.isZoomControlsEnabled = true

        thisMap.setOnMapLongClickListener { addMarker ->
            showBottomSheetView(addMarker)
        }

        thisMap.setOnInfoWindowClickListener { deleteMarker ->
            markersList.remove(deleteMarker)
            deleteMarker.remove()
        }

    }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value
            }
            if (granted) {
                getMyCurrentLocation()
                getMapData()
            } else {
                checkGPSPermission()
            }
        }

    private lateinit var client: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBottomSheetBehavior(binding.includedBottomSheetLayout.bottomSheetContainer)
        client = activity?.let { it1 -> LocationServices.getFusedLocationProviderClient(it1) }!!
        checkGPSPermission() // Запрашиваем все разрешения
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showBottomSheetView(latLng: LatLng) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        binding.includedBottomSheetLayout.addButton.setOnClickListener {
            val snippet = binding.includedBottomSheetLayout.snippetEt.text.toString().trim()
            val title = binding.includedBottomSheetLayout.titleEt.text.toString().trim()
            if (snippet.isNotEmpty() && title.isNotEmpty()) {
                val newMarker = thisMap.addMarker(
                    MarkerOptions().position(latLng)
                        .title(title).snippet(snippet)
                )
                if (newMarker != null) {
                    markersList.add(newMarker)
                }
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                Toast.makeText(context, "Маркер успешно установлен!", Toast.LENGTH_SHORT).show()
                binding.includedBottomSheetLayout.snippetEt.text?.clear()
                binding.includedBottomSheetLayout.titleEt.text?.clear()
            } else if (snippet.isEmpty() || title.isEmpty()) {
                Toast.makeText(context, "Что-то не заполнено!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun activateMyLocation(googleMap: GoogleMap) {
        context?.let {
            val isPermissionGranted =
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) ==
                        PackageManager.PERMISSION_GRANTED

            googleMap.isMyLocationEnabled = isPermissionGranted
            googleMap.uiSettings.isMyLocationButtonEnabled = isPermissionGranted

        }
    }

    private fun checkGPSPermission() {
        context?.let {
            when {
                ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED -> {

                    getMyCurrentLocation()
                    getMapData()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    AlertDialog.Builder(it)
                        .setTitle("Необходим доступ к GPS")
                        .setMessage(
                            "Внимание! Для просмотра данных на карте необходимо разрешение на" +
                                    "использование Вашего местоположения"
                        )
                        .setPositiveButton("Предоставить доступ") { _, _ ->
                            permReqLauncher.launch(PERMISSIONS)
                        }
                        .setNegativeButton("Спасибо, не надо") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                }
                else -> {
                    permReqLauncher.launch(PERMISSIONS)
                }
            }
        }
    }

    private fun showHintSnackBar() {
        mapFragment?.view?.let {
            Snackbar.make(
                it,
                "Для установки маркера удерживайте точку долгим нажатием на карту!",
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction("Понял") {}
                .show()
        }
    }

    private fun getMapData() {
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        showHintSnackBar()
    }

    @SuppressLint("MissingPermission")
    private fun getMyCurrentLocation() {
        val task: Task<Location> = client.lastLocation
        task.addOnSuccessListener { location ->
            if (location !== null) {
                val mapFragment =
                    childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                mapFragment?.getMapAsync {
                    saveMyLocation(location)
                }
            }
        }
    }

    private fun saveMyLocation(location: Location) {
        val loc = LatLng(
            location.latitude,
            location.longitude
        )
        val options: MarkerOptions = MarkerOptions().position(loc)
            .title("I am here bro!")
        thisMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 10f))
        thisMap.addMarker(options)
    }

    private fun setBottomSheetBehavior(bottomSheet: ConstraintLayout) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

//        bottomSheetBehavior.addBottomSheetCallback(object :
//            BottomSheetBehavior.BottomSheetCallback() {
//
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//
//                when (newState) {
//                    BottomSheetBehavior.STATE_HIDDEN -> {}
//                    BottomSheetBehavior.STATE_COLLAPSED -> {}
//                    BottomSheetBehavior.STATE_DRAGGING -> {}
//                    BottomSheetBehavior.STATE_EXPANDED -> {}
//                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {}
//                    BottomSheetBehavior.STATE_SETTLING -> {}
//                }
//            }
//
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//            }
//        })
    }

}