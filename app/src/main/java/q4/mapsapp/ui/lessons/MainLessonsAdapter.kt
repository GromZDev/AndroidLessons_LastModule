package q4.mapsapp.ui.lessons

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import q4.mapsapp.R
import q4.mapsapp.data.Lessons
import q4.mapsapp.data.TYPE
import q4.mapsapp.ui.main.BaseViewHolder

class MainLessonsAdapter(
    private var onItemViewClickListener:
    LessonsFragment.OnItemViewClickListener?
) : RecyclerView.Adapter<BaseViewHolder>() {

    private var lessonsList: List<Lessons> = arrayListOf()
    private var isVisible: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_ONE -> MainLessonsViewHolder(
                inflater.inflate(R.layout.item_lessons_rv, parent, false) as View
            )
            else -> MainLessonsNoVideoViewHolder(
                inflater.inflate(
                    R.layout.item_lessons_no_video_rv, parent,
                    false
                ) as View
            )

        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(lessonsList[position])
    }

    override fun getItemCount(): Int = lessonsList.size

    override fun getItemViewType(position: Int): Int {
        return when (lessonsList[position].type) {
            TYPE.VIDEO -> TYPE_ONE
            else -> TYPE_TWO
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setLessons(organisationsNear: List<Lessons>) {
        this.lessonsList = organisationsNear
        notifyDataSetChanged()
    }

    inner class MainLessonsViewHolder(view: View) :
        BaseViewHolder(view) {

        override fun bind(lessonsData: Lessons) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                itemView.findViewById<ImageView>(R.id.expand_timeline_circle).visibility = View.GONE
                itemView.findViewById<TextView>(R.id.item_name).text = lessonsData.name
                itemView.findViewById<TextView>(R.id.item_time).text = lessonsData.time
                lessonsData.image?.let {
                    itemView.findViewById<ShapeableImageView>(R.id.item_image).setImageResource(it)
                }
                itemView.findViewById<ShapeableImageView>(R.id.item_open_in).setOnClickListener {
                    onItemViewClickListener?.onItemViewClick()
                }
                itemView.findViewById<TextView>(R.id.timeline_tw).text = lessonsData.time
                itemView.findViewById<ConstraintLayout>(R.id.inner_view).setOnClickListener {
                    if (isVisible) {
                        itemView.findViewById<ImageView>(R.id.expand_timeline_circle).visibility = View.GONE
                        itemView.findViewById<ImageView>(R.id.timeline_circle).visibility = View.VISIBLE
                        isVisible = false
                        return@setOnClickListener
                    }
                    itemView.findViewById<ImageView>(R.id.expand_timeline_circle).visibility = View.VISIBLE
                    itemView.findViewById<ImageView>(R.id.timeline_circle).visibility = View.GONE
                    isVisible = true
                }
            }
        }
    }

    inner class MainLessonsNoVideoViewHolder(view: View) :
        BaseViewHolder(view) {

        override fun bind(lessonsData: Lessons) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                itemView.findViewById<ImageView>(R.id.expand_timeline_circle).visibility = View.GONE
                itemView.findViewById<TextView>(R.id.item_name).text = lessonsData.name
                itemView.findViewById<TextView>(R.id.item_time).text = lessonsData.time
                lessonsData.image?.let {
                    itemView.findViewById<ShapeableImageView>(R.id.item_image).setImageResource(it)
                }
                itemView.findViewById<TextView>(R.id.timeline_tw).text = lessonsData.time
                itemView.findViewById<ConstraintLayout>(R.id.inner_view).setOnClickListener {
                    if (isVisible) {
                        itemView.findViewById<ImageView>(R.id.expand_timeline_circle).visibility = View.GONE
                        itemView.findViewById<ImageView>(R.id.timeline_circle).visibility = View.VISIBLE
                        isVisible = false
                        return@setOnClickListener
                    }
                    itemView.findViewById<ImageView>(R.id.expand_timeline_circle).visibility = View.VISIBLE
                    itemView.findViewById<ImageView>(R.id.timeline_circle).visibility = View.GONE
                    isVisible = true
                }
            }
        }
    }

    companion object {
        private const val TYPE_ONE = 0
        private const val TYPE_TWO = 1
    }
}


