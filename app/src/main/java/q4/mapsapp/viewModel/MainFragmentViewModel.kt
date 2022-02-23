package q4.mapsapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import q4.mapsapp.repository.homework.HomeworkRepository
import q4.mapsapp.repository.homework.HomeworkRepositoryImpl
import q4.mapsapp.repository.lessons.LessonsRepository
import q4.mapsapp.repository.lessons.LessonsRepositoryImpl
import q4.mapsapp.ui.main.MainFragmentAppState
import java.lang.Thread.sleep

class MainFragmentViewModel(
    private val liveDataToObserve: MutableLiveData<MainFragmentAppState> =
        MutableLiveData(),
    private val lessonsRepositoryImpl: LessonsRepository = LessonsRepositoryImpl(),
    private val homeworksRepositoryImpl: HomeworkRepository = HomeworkRepositoryImpl()
) : ViewModel() {

    fun getLiveData() = liveDataToObserve

    fun getLessonsFromLocal() = getDataFromLocalSource()


    private fun getDataFromLocalSource() {
        liveDataToObserve.value = MainFragmentAppState.Loading
        Thread {
            sleep(700)
            liveDataToObserve.postValue(
                MainFragmentAppState.Success(
                    lessonsRepositoryImpl.getLessons(),
                    homeworksRepositoryImpl.getHomework()
                )
            )
        }.start()
    }


}