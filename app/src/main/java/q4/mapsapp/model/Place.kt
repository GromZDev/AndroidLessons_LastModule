package q4.mapsapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Place(
    var title: String? = "",
    var snippet: String? = "",
    val location: Location? = null,
    val position: Int? = null
) : Serializable, Parcelable

@Parcelize
data class Location(
    var latitude: Double? = null,
    var longitude: Double? = null,
): Serializable, Parcelable
