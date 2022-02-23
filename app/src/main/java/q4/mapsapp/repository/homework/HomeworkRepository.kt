package q4.mapsapp.repository.homework

import q4.mapsapp.data.Homework
import q4.mapsapp.data.Lessons

interface HomeworkRepository {

    fun getHomework(): List<Homework>
}