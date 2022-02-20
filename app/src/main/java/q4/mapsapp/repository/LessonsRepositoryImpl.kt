package q4.mapsapp.repository

import q4.mapsapp.data.Lessons
import q4.mapsapp.data.getAllLessons

class LessonsRepositoryImpl: LessonsRepository {
    override fun getLessons(): List<Lessons> = getAllLessons()

}