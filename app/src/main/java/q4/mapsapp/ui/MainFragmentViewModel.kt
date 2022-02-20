package q4.mapsapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import q4.mapsapp.repository.LessonsRepository
import q4.mapsapp.repository.LessonsRepositoryImpl
import java.lang.Thread.sleep

class MainFragmentViewModel(
    private val liveDataToObserve: MutableLiveData<MainFragmentAppState> =
        MutableLiveData(),
    private val repositoryImpl: LessonsRepository = LessonsRepositoryImpl()
) : ViewModel() {

    fun getLiveData() = liveDataToObserve

    fun getLessonsFromLocal() = getDataFromLocalSource()


    private fun getDataFromLocalSource() {
        liveDataToObserve.value = MainFragmentAppState.Loading
        Thread {
            sleep(700)
            liveDataToObserve.postValue(
                MainFragmentAppState.Success(
                    repositoryImpl.getLessons()
                )
            )
        }.start()
    }


}