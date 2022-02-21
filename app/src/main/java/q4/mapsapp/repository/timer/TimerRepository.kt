package q4.mapsapp.repository.timer

import q4.mapsapp.data.Homework

interface TimerRepository {
    fun getTimer(
        year: Int,
        month: Int,
        dayOfMonth: Int): Long?
}