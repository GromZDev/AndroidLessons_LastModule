package q4.mapsapp.ui.mainMaps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
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
import org.koin.androidx.viewmodel.ext.android.viewModel
import q4.mapsapp.BaseFragment
import q4.mapsapp.R
import q4.mapsapp.appState.MainMapsAppState
import q4.mapsapp.databinding.FragmentMainMapsBinding
import q4.mapsapp.model.Place
import q4.mapsapp.ui.markerList.MyListFragment

class MainMapsFragment(override val layoutId: Int = R.layout.fragment_main_maps) :
    BaseFragment<FragmentMainMapsBinding>() {

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

    private lateinit var thisMap: GoogleMap
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var markersList: MutableList<Marker> = mutableListOf()
    private var mapFragment: SupportMapFragment? = null
    private var list: MutableList<Place> = mutableListOf()
    private var placesFromList: MutableList<Place> = mutableListOf()
    private val viewModel by viewModel<MainMapsViewModel>()

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
            getSavedDataFromStorage(placesFromList)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        binding?.includedBottomSheetLayout?.bottomSheetContainer?.let { setBottomSheetBehavior(it) }
        client = activity?.let { it1 -> LocationServices.getFusedLocationProviderClient(it1) }!!
        checkGPSPermission()

        if (arguments !== null) {
            placesFromList = arguments?.getParcelableArrayList(BUNDLE_EXTRA)!!
            Log.i("TAG", "$placesFromList Получен в Главном <<<<<<<<<<<<<")
        } else if (arguments == null && placesFromList.isNullOrEmpty()) {
            showHintSnackBar()
        }

        viewModel.getLiveData().observe(viewLifecycleOwner, { renderData(it) })
        viewModel.getMarkersData()
    }

    private fun renderData(appState: MainMapsAppState) {
        when (appState) {
            is MainMapsAppState.Success -> {

                binding?.includedLoadingLayout?.loadingLayout?.visibility = View.GONE
                getSavedDataFromStorage(appState.markersData)
            }
            is MainMapsAppState.Loading -> {
                binding?.includedLoadingLayout?.loadingLayout?.visibility = View.VISIBLE

            }
            is MainMapsAppState.Error -> {

            }
        }
    }

    /** Данные из хранилища ---------------- */
    private fun getSavedDataFromStorage(appState: List<Place>) {

        placesFromList.addAll(appState)
        for (places in placesFromList) {
            viewModel.getLatLn(places).apply {
                this?.let {
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

    /** --------------------------------------- */
    private fun getMyPlacesFromList() {
        for (places in placesFromList) {
            viewModel.getLatLn(places).apply {
                this?.let {
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

    private fun showBottomSheetView(latLng: LatLng) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        binding?.includedBottomSheetLayout?.addButton?.setOnClickListener {
            val snippet = binding!!.includedBottomSheetLayout.snippetEt.text.toString().trim()
            val title = binding!!.includedBottomSheetLayout.titleEt.text.toString().trim()
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
                binding!!.includedBottomSheetLayout.snippetEt.text?.clear()
                binding!!.includedBottomSheetLayout.titleEt.text?.clear()
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

             viewModel.makeBundleAndGoToListFragment(markersList).apply {
                 val bundle = Bundle()
                 bundle.putParcelableArrayList(
                     MyListFragment.BUNDLE_EXTRA,
                     this as java.util.ArrayList<Place>
                 )

                 parentFragmentManager.beginTransaction()
                     .replace(R.id.fragment_container, MyListFragment.newInstance(bundle))
                     .addToBackStack("")
                     .commitAllowingStateLoss()
             }

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