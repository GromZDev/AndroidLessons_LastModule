package q4.mapsapp.data

import q4.mapsapp.R

data class Lessons(
    val id: Int,
    val name: String = "",
    val image: Int? = null,
    val time: String = ""
)

fun getAllLessons() = mutableListOf(
    Lessons(1,"Математика", R.drawable.fallout_1, "8.30"),
    Lessons(2,"Физика", R.drawable.fallout_2, "9.30"),
    Lessons(3,"История", R.drawable.fallout_3, "10.30"),
    Lessons(4,"Литература", R.drawable.fallout_4, "11.30"),
    Lessons(5,"Английский", R.drawable.fallout_5, "12.45")
)
