package q4.mapsapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import q4.mapsapp.databinding.FragmentMainMapsBinding
import java.util.*

class MainMapsFragment: Fragment(R.layout.fragment_main_maps) {

    companion object {
        var PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION)
        fun newInstance() = MainMapsFragment()
    }

    private var _binding: FragmentMainMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var thisMap: GoogleMap

    private val callback = OnMapReadyCallback { googleMap ->
        thisMap = googleMap
        activateMyLocation(thisMap)
        thisMap.uiSettings.isZoomControlsEnabled = true

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
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        client = activity?.let { it1 -> LocationServices.getFusedLocationProviderClient(it1) }!!
        checkGPSPermission() // Запрашиваем все разрешения
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
                // Метод для нас, чтобы знали когда необходимы пояснения показывать перед запросом:
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

    private fun getMapData() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
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

}