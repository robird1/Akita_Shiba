package com.ulsee.ulti_a100.ui.people

import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ulsee.ulti_a100.R
import com.ulsee.ulti_a100.model.People

private val TAG = PeopleAdapter::class.java.simpleName

class PeopleAdapter(private val fragment: PeopleFragment): RecyclerView.Adapter<PeopleAdapter.ViewHolder>() {

    var peopleList: MutableList<People> = ArrayList()
    fun setList(list: List<People>) {
//        peopleList = list
        peopleList.clear()
        peopleList.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = this.peopleList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "[Enter] onBindViewHolder position: $position")
        holder.bind(peopleList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(TAG, "[Enter] onCreateViewHolder")
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_people2, parent, false)
        return ViewHolder(view)
    }

    fun removeItem(position: Int) {
        peopleList.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTV = itemView.findViewById<TextView>(R.id.textView_name)
        private val workIdTV = itemView.findViewById<TextView>(R.id.textView_workID)
        private val faceView = itemView.findViewById<ImageView>(R.id.face_img)
        val viewForeground: ConstraintLayout = itemView.findViewById(R.id.view_foreground)
        val deleteIconRight: ImageView = itemView.findViewById(R.id.delete_icon_right)
        private var data: People? = null

        init {
            Log.d(TAG, "[Enter] init in ViewHolder")
            itemView.setOnClickListener {
                data?.let { it1 -> fragment.openEditor(it1, true) }
            }
        }

        fun bind(people: People) {
            if (people != null) {
                data = people
                nameTV.text = people.getName()
                workIdTV.text = people.getWorkID()
                Glide.with(itemView.context).load(Base64.decode(people.getFaceImg(), Base64.DEFAULT)).into(faceView)
            }
        }
    }
}


