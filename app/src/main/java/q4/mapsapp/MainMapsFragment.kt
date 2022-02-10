package q4.mapsapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
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
import q4.mapsapp.model.Place
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream

class MainMapsFragment : Fragment() {

    companion object {
        var PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        const val BUNDLE_EXTRA = "MY_List_Markers"
        fun newInstance() = MainMapsFragment()
        fun newInstance(bundle: Bundle?): MainMapsFragment {
            val fragment = MainMapsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var _binding: FragmentMainMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var thisMap: GoogleMap
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var markersList: MutableList<Marker> = mutableListOf()
    private var mapFragment: SupportMapFragment? = null
    private var list: MutableList<Place> = mutableListOf()
    private var placesFromList: MutableList<Place> = mutableListOf()

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

        if (markersList.isEmpty()) {
            getSavedDataFromStorage()
        }

        getMyPlacesFromList()

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

        setHasOptionsMenu(true)
        setBottomSheetBehavior(binding.includedBottomSheetLayout.bottomSheetContainer)
        client = activity?.let { it1 -> LocationServices.getFusedLocationProviderClient(it1) }!!
        checkGPSPermission()

        if (arguments !== null) {
            placesFromList = arguments?.getParcelableArrayList(BUNDLE_EXTRA)!!
            Log.i("TAG", "$placesFromList Получен в Главном <<<<<<<<<<<<<")
        }
        else if (arguments == null && placesFromList.isNullOrEmpty()) {
            showHintSnackBar()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /** Данные из хранилища ---------------- */
    private fun getSavedDataFromStorage() {
        val dataFromFile = context?.let { deserializeDataFromFile(it).toMutableList() }
        if (dataFromFile != null) {

            placesFromList.addAll(dataFromFile)

            for (places in placesFromList) {
                val latLng = places.location?.latitude?.let {
                    places.location.longitude?.let { it1 ->
                        LatLng(
                            it,
                            it1
                        )
                    }
                }
                latLng?.let {
                    MarkerOptions().position(it)
                        .title(places.title).snippet(places.snippet)
                }?.let {
                    thisMap.addMarker(
                        it
                    )
                }
            }
        }
    }

    private fun getDataFromFile(context: Context): File {
        return File(context.filesDir, FILENAME)
    }

    private fun deserializeDataFromFile(context: Context): MutableList<Place> {
        val dataFile = getDataFromFile(context)
        if (!dataFile.exists()) {
            return mutableListOf()
        }
        ObjectInputStream(FileInputStream(dataFile)).use { return it.readObject() as MutableList<Place> }
    }
    /** ---------------------------- */

    private fun getMyPlacesFromList() {
        for (places in placesFromList) {
            val latLng = places.location?.latitude?.let {
                places.location.longitude?.let { it1 ->
                    LatLng(
                        it,
                        it1
                    )
                }
            }
            latLng?.let {
                MarkerOptions().position(it)
                    .title(places.title).snippet(places.snippet)
            }?.let {
                thisMap.addMarker(
                    it
                )
            }
        }
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
                    Log.i("TAG", newMarker.title.toString() + " !!!!!!!!!!!!")

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
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_screen_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_show_list -> {
                if (markersList.isEmpty()) {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, MyListFragment.newInstance())
                        .addToBackStack("")
                        .commitAllowingStateLoss()
                } else if (markersList.isNotEmpty()) {


                    val places = markersList.map { marker ->
                        Place(
                            marker.title,
                            marker.snippet,
                            q4.mapsapp.model.Location(
                                marker.position.latitude,
                                marker.position.longitude
                            )
                        )
                    }
                    val bundle = Bundle()
                    list.addAll(places)
                    bundle.putParcelableArrayList(
                        MyListFragment.BUNDLE_EXTRA,
                        list as java.util.ArrayList<Place>
                    )

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, MyListFragment.newInstance(bundle))
                        .addToBackStack("")
                        .commitAllowingStateLoss()
                }
                true
            }

            R.id.menu_show_map -> {
                parentFragmentManager.apply {
                    beginTransaction()
                        .replace(R.id.fragment_container, newInstance())
                        .addToBackStack("")
                        .commitAllowingStateLoss()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}