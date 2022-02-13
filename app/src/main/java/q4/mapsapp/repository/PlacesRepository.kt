package q4.mapsapp.repository

import q4.mapsapp.model.Place

interface PlacesRepository {

    fun getAllPlacesData(): List<Place>

}