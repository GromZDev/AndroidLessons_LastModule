package q4.mapsapp.repository

import q4.mapsapp.data.Lessons

interface LessonsRepository {

    fun getLessons(): List<Lessons>
}