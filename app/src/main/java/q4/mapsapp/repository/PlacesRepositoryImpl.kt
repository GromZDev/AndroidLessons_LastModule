package q4.mapsapp.repository

import android.content.Context
import q4.mapsapp.ui.markerList.FILENAME
import q4.mapsapp.model.Place
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream

class PlacesRepositoryImpl(
    private val context: Context
) : PlacesRepository {

    override fun getAllPlacesData(): List<Place> = getAllPlacesFromFile()

    private fun getAllPlacesFromFile(): MutableList<Place> {
        val list = mutableListOf<Place>()
        val dataFromFile = context.let { deserializeDataFromFile(it).toMutableList() }
        if (dataFromFile != null) {
            list.addAll(dataFromFile)
        }
        return list
    }

    private fun getDataFromFile(context: Context): File {
        return File(context.filesDir, FILENAME)
    }

    private fun deserializeDataFromFile(context: Context): MutableList<Place> {
        val dataFile = getDataFromFile(context)
        if (!dataFile.exists()) {
            return mutableListOf()
        }
        ObjectInputStream(FileInputStream(dataFile)).use { return it.readObject() as MutableList<Place> }
    }
}