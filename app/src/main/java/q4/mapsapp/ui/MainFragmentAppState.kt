package q4.mapsapp.ui

import q4.mapsapp.data.Lessons

sealed class MainFragmentAppState {
    data class Success (val lessonsData: List<Lessons>): MainFragmentAppState()
    data class Error (val error: Throwable): MainFragmentAppState()
    object Loading: MainFragmentAppState()

}