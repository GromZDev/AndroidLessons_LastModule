package q4.mapsapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Place(
    val title: String? = "",
    val snippet: String? = "",
    val location: Location? = null
) : Parcelable

@Parcelize
data class Location(
    var latitude: Double? = null,
    var longitude: Double? = null,
): Parcelable
