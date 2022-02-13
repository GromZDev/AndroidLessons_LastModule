package q4.mapsapp.ui.markerList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import q4.mapsapp.R
import q4.mapsapp.databinding.ItemMarkersRvBinding
import q4.mapsapp.model.Place

class AllMarkersAdapter(
    private var onItemViewClickListener: MyListFragment.OnItemViewClickListener?
) :
    RecyclerView.Adapter<AllMarkersAdapter.AllMarkersViewHolder>(),
    ItemTouchHelperAdapter {

    private var allMarkersList: MutableList<Place> = arrayListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = AllMarkersViewHolder(
        ItemMarkersRvBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
    )

    override fun getItemCount(): Int = allMarkersList.size

    override fun onBindViewHolder(holder: AllMarkersViewHolder, position: Int) {
        holder.bind(allMarkersList[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAllMarkersData(data: MutableList<Place>) {
        this.allMarkersList = data
        notifyDataSetChanged()
    }

    fun setNewData(title: String, desc: String, position: Int) {
        allMarkersList[position].title = title
        allMarkersList[position].snippet = desc
        notifyItemChanged(position)
    }

    fun appendItem(markerData: Place) {
        allMarkersList.add(markerData)
        notifyItemInserted(itemCount - 1) // С анимацией добавления
    }

    inner class AllMarkersViewHolder(
        private val vb: ItemMarkersRvBinding
    ) :
        RecyclerView.ViewHolder(vb.root), ItemTouchHelperViewHolder {

        fun bind(data: Place) = with(vb) {
            vb.itemTitle.text = data.title
            vb.itemOverview.text = data.snippet
            //  vb.itemAddress.text = data.location
            itemView.setOnClickListener {
                onItemViewClickListener?.onMarkerItemViewClick(data, layoutPosition) // Вызываем слушатель нажатия
            }
        }

        override fun onItemSelected() {
            itemView.setBackgroundResource(R.drawable.button_background)
        }

        override fun onItemClear() {
            itemView.setBackgroundResource(R.drawable.markers_rv_background)
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {}

    override fun onItemDismiss(position: Int) {
        allMarkersList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeListener() {
        onItemViewClickListener = null
    }

}
