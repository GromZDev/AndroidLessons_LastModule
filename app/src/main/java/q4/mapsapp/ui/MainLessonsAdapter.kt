package q4.mapsapp.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import q4.mapsapp.data.Lessons
import q4.mapsapp.databinding.ItemLessonsRvBinding

class MainLessonsAdapter : RecyclerView.Adapter<MainLessonsAdapter.MainLessonsViewHolder>() {

    private var lessonsList: List<Lessons> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MainLessonsViewHolder(
        ItemLessonsRvBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
    )

    override fun getItemCount(): Int = lessonsList.size

    override fun onBindViewHolder(holder: MainLessonsViewHolder, position: Int) {
        holder.bind(lessonsList[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setLessons(organisationsNear: List<Lessons>) {
        this.lessonsList = organisationsNear
        notifyDataSetChanged()
    }

    inner class MainLessonsViewHolder(private val vb: ItemLessonsRvBinding) :
        RecyclerView.ViewHolder(vb.root) {


        fun bind(lessons: Lessons) = with(vb) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                itemName.text = lessons.name
                itemTime.text = lessons.time
                lessons.image?.let { itemImage.setImageResource(it) }
            }
        }
    }

}
