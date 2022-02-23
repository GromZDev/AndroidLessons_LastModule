package q4.mapsapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import q4.mapsapp.repository.timer.TimerRepository
import q4.mapsapp.repository.timer.TimerRepositoryImpl
import q4.mapsapp.ui.main.TimerAppState

class TimerViewModel(
    private val liveDataToObserve: MutableLiveData<TimerAppState> =
        MutableLiveData(),
    private val timerRepositoryImpl: TimerRepository = TimerRepositoryImpl()
) : ViewModel() {

    fun getLiveData() = liveDataToObserve

    fun getTimerData(
        year: Int,
        month: Int,
        dayOfMonth: Int
    ) = getDataFromLocalSource(year, month, dayOfMonth)

    private fun getDataFromLocalSource(
        year: Int,
        month: Int,
        dayOfMonth: Int
    ) {
        liveDataToObserve.value = TimerAppState.Loading
        Thread {
            Thread.sleep(700)
            liveDataToObserve.postValue(
                timerRepositoryImpl.getTimer(year, month, dayOfMonth)?.let {
                    TimerAppState.Success(
                        it
                    )
                }
            )
        }.start()
    }
}
