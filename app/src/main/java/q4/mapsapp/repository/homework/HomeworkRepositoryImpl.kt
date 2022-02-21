package q4.mapsapp.repository.homework

import q4.mapsapp.data.Homework
import q4.mapsapp.data.Lessons
import q4.mapsapp.data.getAllHomeworks
import q4.mapsapp.data.getAllLessons

class HomeworkRepositoryImpl: HomeworkRepository {
    override fun getHomework(): List<Homework> = getAllHomeworks()




}