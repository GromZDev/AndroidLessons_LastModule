package q4.mapsapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import q4.mapsapp.repository.lessons.LessonsRepository
import q4.mapsapp.repository.lessons.LessonsRepositoryImpl
import q4.mapsapp.ui.lessons.LessonsFragmentAppState
import java.lang.Thread.sleep

class LessonsFragmentViewModel(
    private val liveDataToObserve: MutableLiveData<LessonsFragmentAppState> =
        MutableLiveData(),
    private val lessonsRepositoryImpl: LessonsRepository = LessonsRepositoryImpl()
) : ViewModel() {

    fun getLiveData() = liveDataToObserve

    fun getLessonsFromLocal() = getDataFromLocalSource()


    private fun getDataFromLocalSource() {
        liveDataToObserve.value = LessonsFragmentAppState.Loading
        Thread {
            sleep(700)
            liveDataToObserve.postValue(
                LessonsFragmentAppState.Success(
                    lessonsRepositoryImpl.getLessons()
                )
            )
        }.start()
    }


}