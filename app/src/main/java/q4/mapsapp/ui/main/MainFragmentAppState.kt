package q4.mapsapp.ui.main

import q4.mapsapp.data.Homework
import q4.mapsapp.data.Lessons

sealed class MainFragmentAppState {
    data class Success (val lessonsData: List<Lessons>,
                        val homeworkData: List<Homework>
                        ): MainFragmentAppState()
    data class Error (val error: Throwable): MainFragmentAppState()
    object Loading: MainFragmentAppState()

}