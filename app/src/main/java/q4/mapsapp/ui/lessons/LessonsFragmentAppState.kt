package q4.mapsapp.ui.lessons

import q4.mapsapp.data.Homework
import q4.mapsapp.data.Lessons

sealed class LessonsFragmentAppState {
    data class Success (val lessonsData: List<Lessons>
                        ): LessonsFragmentAppState()
    data class Error (val error: Throwable): LessonsFragmentAppState()
    object Loading: LessonsFragmentAppState()

}