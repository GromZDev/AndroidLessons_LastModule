package q4.mapsapp.di

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.component.KoinApiExtension
import org.koin.dsl.module
import q4.mapsapp.ui.mainMaps.MainMapsViewModel
import q4.mapsapp.repository.PlacesRepositoryImpl

@OptIn(KoinApiExtension::class)
val appModule = module {

    single { PlacesRepositoryImpl(get()) }

    viewModel { MainMapsViewModel(androidApplication()) }

}