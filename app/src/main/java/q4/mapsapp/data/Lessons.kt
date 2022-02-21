package q4.mapsapp.data

import q4.mapsapp.R

data class Lessons(
    val id: Int,
    val name: String = "",
    val image: Int? = null,
    val time: String = "",
    val type: TYPE? = null
)

enum class TYPE {
    VIDEO,
    NO_VIDEO
}

fun getAllLessons() = mutableListOf(
    Lessons(1,"Математика", R.drawable.fallout_1, "8.30", TYPE.VIDEO),
    Lessons(2,"Физика", R.drawable.fallout_2, "9.30"),
    Lessons(3,"История", R.drawable.fallout_3, "10.30", TYPE.VIDEO),
    Lessons(4,"Литература", R.drawable.fallout_4, "11.30", TYPE.VIDEO),
    Lessons(5,"Английский", R.drawable.fallout_5, "12.45"),
    Lessons(6,"Рисование", R.drawable.fallout_1, "13.45"),
    Lessons(7,"Kotlin", R.drawable.fallout_3, "14.45", TYPE.VIDEO)
)
