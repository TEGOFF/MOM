package ModulesAndAdapters

import DataClasses.DairyTaskData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.tm.Fragments.DoneTasksFragment
import com.example.tm.Fragments.HomeFragment
import com.example.tm.R
import com.example.tm.databinding.EachTaskItemBinding
import com.google.android.gms.tasks.Task

class DairyTaskAdapter(private val list:MutableList<DairyTaskData>) : Adapter<DairyTaskAdapter.TaskViewHolder>()
{
    private var listener: DairyTaskAdapterClickInterface?=null


    fun setListener(listener: HomeFragment){
        this.listener=listener
    }
    fun setListener(listener: DoneTasksFragment){
        this.listener=listener
    }





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {

        val binding =
            EachTaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        if(list.isNotEmpty()){
            with(holder){
                with(list[position]) {
                    binding.DairyTaskName.text = this.dairyTaskName
                    binding.tvCategory.text = this.category

                    if (this.notificationTime != "Not set") {
                        binding.tvTime.setText(this.notificationTime)
                    }
                    if(list[position].isDone){
                        binding.isDoneCheckBox.isChecked=true
                    }
                    else{
                        binding.isDoneCheckBox.isChecked=false
                    }
                    if (list[position].containsSub) {
                        binding.ivSubIcon.visibility = View.VISIBLE
                    }


                    binding.isDoneCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
                        listener?.onCheckBoxClicked(this, position)
                    }

                    binding.editTask.setOnClickListener() {
                        listener?.onEditTaskButtonClicked(this)
                    }
                    binding.EachItemDairyTask.setOnClickListener() {
                        listener?.onTaskClicked(this)
                    }
                }
            }
        }

    }
    inner class TaskViewHolder(val binding:EachTaskItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val checkbox: CheckBox = itemView.findViewById(R.id.isDoneCheckBox)

        fun bind(task: DairyTaskData) {

            checkbox.isChecked = task.isDone

            checkbox.setOnCheckedChangeListener { _, isChecked ->
                // Update dataset and notify adapter when checkbox state changes
                task.isDone = isChecked
                listener?.onCheckBoxClicked(task, adapterPosition)
            }
        }
    }
    interface DairyTaskAdapterClickInterface{
        fun onTaskClicked(taskData: DairyTaskData)
        fun onEditTaskButtonClicked(taskData: DairyTaskData)

        fun onCheckBoxClicked(taskData: DairyTaskData, position: Int)


    }

}