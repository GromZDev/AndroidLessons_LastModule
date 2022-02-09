package q4.mapsapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import q4.mapsapp.databinding.FragmentMyListBinding
import q4.mapsapp.model.Place

class MyListFragment : Fragment() {

    private lateinit var markersList: MutableList<Place>
    private lateinit var markersAdapter: AllMarkersAdapter
    private lateinit var recyclerView: RecyclerView

    private var _binding: FragmentMyListBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val BUNDLE_EXTRA = "MY_Markers"
        fun newInstance() = MyListFragment()
        fun newInstance(bundle: Bundle?): MyListFragment {
            val fragment = MyListFragment()
            fragment.arguments = bundle
            return fragment
        }
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

        // rv set
        recyclerView = binding.markersListRv
        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.setHasFixedSize(true)
        markersList = arrayListOf()
        markersAdapter = AllMarkersAdapter()
        recyclerView.adapter = markersAdapter
        // ______________

        if (arguments !== null) {
            val a = arguments?.getParcelableArrayList<Place>(BUNDLE_EXTRA)
            Log.i("TAG", a?.get(0)?.title.toString() + " Получен!!!!!!!!!!!!")

            if (a != null) {
                (markersList as ArrayList<Place>).addAll(a)
            }

            markersAdapter.setAllMarkersData(markersList)
            //  markersAdapter.notifyItemInserted(markersList.size -1)
        }


        /** ======= Сетим ItemTouchHelper в наш ресайклер для смахивания и таскания ======== */
        val swipeToDelete = object : ItemTouchHelperCallback(markersAdapter) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {

                markersAdapter.onItemDismiss(viewHolder.adapterPosition)
                Toast.makeText(context, "Маркер удален!", Toast.LENGTH_SHORT).show()
            }
        }
        val itemTH = ItemTouchHelper(swipeToDelete)
        itemTH.attachToRecyclerView(recyclerView)
        /** ================================================================================ */
    }
}