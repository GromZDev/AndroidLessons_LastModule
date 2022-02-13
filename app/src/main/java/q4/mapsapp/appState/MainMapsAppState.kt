package q4.mapsapp.appState

import q4.mapsapp.model.Place

sealed class MainMapsAppState {
    data class Success(val markersData: List<Place>) : MainMapsAppState()
    data class Error(val error: Throwable) : MainMapsAppState()
    object Loading : MainMapsAppState()
}