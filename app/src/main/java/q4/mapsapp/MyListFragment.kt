package q4.mapsapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import q4.mapsapp.databinding.FragmentMyListBinding
import q4.mapsapp.model.Location
import q4.mapsapp.model.Place
import java.io.*

const val FILENAME = "MyPlacesList.data"

class MyListFragment : Fragment() {

    private lateinit var markersList: MutableList<Place>
    private lateinit var markersAdapter: AllMarkersAdapter
    private lateinit var recyclerView: RecyclerView
    private var allMarkersInRvList: MutableList<Place> = mutableListOf()

    private var _binding: FragmentMyListBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val BUNDLE_EXTRA = "MY_Markers"
        const val BUNDLE_EXTRA_EDIT = "MY_Markers_Edit"
        fun newInstance() = MyListFragment()
        fun newInstance(bundle: Bundle?): MyListFragment {
            val fragment = MyListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    interface OnItemViewClickListener {
        fun onMarkerItemViewClick(place: Place, position: Int)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        // rv set
        recyclerView = binding.markersListRv
        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.setHasFixedSize(true)
        markersList = arrayListOf()
        markersAdapter = AllMarkersAdapter(object : OnItemViewClickListener {
            override fun onMarkerItemViewClick(place: Place, position: Int) {
                val manager = activity?.supportFragmentManager
                manager?.let {
                    val placeToDetails = Place(place.title, place.snippet,
                        Location(place.location?.latitude, place.location?.longitude), position = position)
                    val bundle = Bundle()
                    bundle.putParcelable(MarkerDetailFragment.BUNDLE_EXTRA, placeToDetails)
                    manager.beginTransaction()
                        .replace(R.id.fragment_container, MarkerDetailFragment.newInstance(bundle))
                        .addToBackStack("")
                        .commitAllowingStateLoss()
                }
            }

        })
        recyclerView.adapter = markersAdapter
        // ______________

        getSavedDataFromStorage()

        if (arguments !== null) {
            val a = arguments?.getParcelableArrayList<Place>(BUNDLE_EXTRA)
            Log.i("TAG", a?.get(0)?.title.toString() + " Получен!!!!!!!!!!!!")

            if (a != null) {
                (markersList as ArrayList<Place>).addAll(a)
            }

            markersAdapter.setAllMarkersData(markersList)

        }
        if (markersList.isEmpty()) {
            Toast.makeText(context, "Нет установленного маркера!", Toast.LENGTH_SHORT)
                .show()
        }

        if (arguments !== null) {
            val b = arguments?.getParcelable<Place>(BUNDLE_EXTRA_EDIT)
            Log.i("TAG", b?.toString() + " Получены Измененные данные!!!!!!!!!!!!")

            if (b != null) {
                b.title?.let { b.snippet?.let { it1 ->
                    b.position?.let { it2 ->
                        markersAdapter.setNewData(it,
                            it1, it2
                        )
                        markersAdapter.notifyItemChanged(it2)
                    }
                } }
            }
        }



        saveDataToStorage()


        /** ======= Сетим ItemTouchHelper в наш ресайклер для смахивания и таскания ======== */
        val swipeToDelete = object : ItemTouchHelperCallback(markersAdapter) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {

                markersAdapter.onItemDismiss(viewHolder.adapterPosition)
                Toast.makeText(context, "Маркер удален!", Toast.LENGTH_SHORT).show()
                saveDataToStorage()
            }
        }
        val itemTH = ItemTouchHelper(swipeToDelete)
        itemTH.attachToRecyclerView(recyclerView)
        /** ================================================================================ */
    }

    private fun saveDataToStorage() {
        context?.let { serializeDataToFile(it, markersList) }
    }

    private fun getSavedDataFromStorage() {
        /** Даные из файла:*/
        val dataFromFile = context?.let { deserializeDataFromFile(it).toMutableList() }
        if (dataFromFile != null) {
            markersList.addAll(dataFromFile)
            markersAdapter.setAllMarkersData(markersList)
        }
        /** */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        markersAdapter.removeListener()
        super.onDestroy()
    }

    private fun getDataFromFile(context: Context): File {
        return File(context.filesDir, FILENAME)
    }

    private fun serializeDataToFile(context: Context, placesList: MutableList<Place>) {
        ObjectOutputStream(FileOutputStream(getDataFromFile(context))).use {
            it.writeObject(
                placesList
            )
        }
    }

    private fun deserializeDataFromFile(context: Context): MutableList<Place> {
        val dataFile = getDataFromFile(context)
        if (!dataFile.exists()) {
            return mutableListOf()
        }
        ObjectInputStream(FileInputStream(dataFile)).use { return it.readObject() as MutableList<Place> }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_screen_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_show_list -> {
                if (markersList.isEmpty()) {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, newInstance())
                        .addToBackStack("")
                        .commitAllowingStateLoss()
                }
                true
            }

            R.id.menu_show_map -> {
                if (markersList.isEmpty()) {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, MainMapsFragment.newInstance())
                        .addToBackStack("")
                        .commitAllowingStateLoss()
                } else if (markersList.isNotEmpty()) {

                    val bundle = Bundle()
                    allMarkersInRvList.addAll(markersList)
                    bundle.putParcelableArrayList(
                        MainMapsFragment.BUNDLE_EXTRA,
                        allMarkersInRvList as java.util.ArrayList<Place>
                    )

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, MainMapsFragment.newInstance(bundle))
                        .addToBackStack("")
                        .commitAllowingStateLoss()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}