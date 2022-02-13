package q4.mapsapp.ui.mainMaps

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import q4.mapsapp.appState.MainMapsAppState
import q4.mapsapp.model.Place
import q4.mapsapp.repository.PlacesRepository
import q4.mapsapp.repository.PlacesRepositoryImpl

class MainMapsViewModel(
    app: Application,
    private val liveDataToObserve: MutableLiveData<MainMapsAppState> =
        MutableLiveData(),
    private val placesRepositoryImpl: PlacesRepository = PlacesRepositoryImpl(app)
) : ViewModel() {

    fun getLiveData() = liveDataToObserve

    fun getMarkersData() = getDataFromFile()

    private fun getDataFromFile() {
        liveDataToObserve.value = MainMapsAppState.Loading
            liveDataToObserve.postValue(
                MainMapsAppState.Success(
                    placesRepositoryImpl.getAllPlacesData(),
                )
            )
    }

    fun getLatLn(places: Place): LatLng? {
        val latLng = places.location?.latitude?.let {
            places.location.longitude?.let { it1 ->
                LatLng(
                    it,
                    it1
                )
            }
        }
        return latLng
    }

    fun makeBundleAndGoToListFragment(markersList: List<Marker>): MutableList<Place> {
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
        val list: MutableList<Place> = mutableListOf()
        list.addAll(places)
        return list
    }
}