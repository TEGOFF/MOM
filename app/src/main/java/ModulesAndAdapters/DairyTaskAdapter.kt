package ModulesAndAdapters

import DataClasses.DairyTaskData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.tm.Fragments.DoneTasksFragment
import com.example.tm.Fragments.HomeFragment
import com.example.tm.databinding.EachTaskItemBinding

class DairyTaskAdapter(private val list:MutableList<DairyTaskData>) : Adapter<DairyTaskAdapter.TaskViewHolder>()
{
    private var listener: DairyTaskAdapterClickInterface?=null


    fun setListener(listener: HomeFragment){
        this.listener=listener
    }
    fun setListener(listener: DoneTasksFragment){
        this.listener=listener
    }

    class TaskViewHolder(val binding:EachTaskItemBinding):RecyclerView.ViewHolder(binding.root)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {

        val binding =
            EachTaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                binding.DairyTaskName.text=this.dairyTaskName
                binding.tvCategory.text = this.category

                if(this.notificationTime != "Not set"){
                    binding.tvTime.setText(this.notificationTime)
                }

                if(list[position].containsSub){
                    binding.ivSubIcon.visibility = View.VISIBLE
                }
                if(this.isDone)
                    binding.isDoneCheckBox.isChecked=true

                binding.editTask.setOnClickListener(){
                    listener?.onEditTaskButtonClicked(this)
                }
                binding.EachItemDairyTask.setOnClickListener(){
                    listener?.onTaskClicked(this)
                }
            }
        }

    }
    interface DairyTaskAdapterClickInterface{
        fun onTaskClicked(taskData: DairyTaskData)
        fun onEditTaskButtonClicked(taskData: DairyTaskData)


    }

}