package q4.mapsapp.ui.main

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import q4.mapsapp.data.Lessons

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(lessonsData: Lessons)


}