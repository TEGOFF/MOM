package com.example.tm.utilities

import DataClasses.DairyTaskData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.tm.Fragments.TaskDescriptionFragment
import com.example.tm.R
import com.example.tm.databinding.EachTaskItemBinding

class SubTasksAdapter(val list: MutableList<DairyTaskData>):
    RecyclerView.Adapter<SubTasksAdapter.ViewHolder>() {

        private var listener: OnClickInterface? = null

    class ViewHolder (viewItem: View): RecyclerView.ViewHolder(viewItem) {
        val name = viewItem.findViewById<TextView>(R.id.DairyTaskName)
        val edit: ImageView = viewItem.findViewById(R.id.editTask)
        val delete: ImageView = viewItem.findViewById(R.id.deleteTask)
    }

    fun setListener(fragment: TaskDescriptionFragment){
        this.listener = fragment
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.each_task_item, parent,false)

        return ViewHolder(item)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = list[position].dairyTaskName

        holder.delete.setOnClickListener{
            listener!!.onDeleteClicked(this.list[position], position)
        }
        holder.edit.setOnClickListener{
            listener!!.onEditClicked(this.list[position], position)
        }
    }
}

interface OnClickInterface{
    fun onDeleteClicked(task: DairyTaskData, position: Int)

    fun onEditClicked(task: DairyTaskData, position: Int)
}