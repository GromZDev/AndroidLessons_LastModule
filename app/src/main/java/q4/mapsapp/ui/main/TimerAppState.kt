package q4.mapsapp.ui.main

import q4.mapsapp.data.Homework
import q4.mapsapp.data.Lessons

sealed class TimerAppState {
    data class Success (val timerData: Long
    ): TimerAppState()
    data class Error (val error: Throwable): TimerAppState()
    object Loading: TimerAppState()
}