package com.example.tm.Fragments

import ModulesAndAdapters.FireHelper
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tm.databinding.FragmentTaskDescriptionBinding

import DataClasses.DairyTaskData



import com.example.tm.utilities.OnClickInterface
import com.example.tm.utilities.SubTasksAdapter

import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat


class TaskDescriptionFragment : DialogFragment(), OnClickInterface {

    private var dairyTaskData: DairyTaskData?=null
    private var listener :AddTaskPopUpFragment.DialogBtnClickListeners?=null
    private lateinit var binding: FragmentTaskDescriptionBinding
    lateinit var adapter: SubTasksAdapter
    val list: MutableList<DairyTaskData> = mutableListOf()
    var completedTasks:Boolean=false


    lateinit var subTaskEditText: EditText
    lateinit var submit: Button

    var status: String = ""
    var position: Int = -1
    fun setListener(listener: HomeFragment) {
        this.listener = listener
        completedTasks=false
    }
    fun setListener(listener: DoneTasksFragment){
        this.listener=listener
        completedTasks=true

    }
    companion object {
        const val TAG = "DialogFragment"
        @JvmStatic
        fun newInstance(task: String, taskDescription: String, taskId: String):TaskDescriptionFragment =
            TaskDescriptionFragment().apply {
                arguments = Bundle().apply {
                    putString("dairyTaskName", task)
                    putString("dairyTaskDescription", taskDescription)
                    putString("dairyTaskId", taskId)
                }
            }


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentTaskDescriptionBinding.inflate(inflater, container, false)
        return binding.root


    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var time:String=""
        var date:String=""
        if(completedTasks){
            binding.BtnTaskSave.visibility=View.GONE
            binding.TimeSetter.visibility=View.GONE
            binding.ivAddST.visibility=View.GONE
        }

        submit = binding.btSubmitST
        subTaskEditText = binding.etSubTask

        if (arguments != null) {
            dairyTaskData = DairyTaskData(
                arguments?.getString("dairyTaskName").toString(),
                arguments?.getString("dairyTaskDescription").toString(),
                arguments?.getString("dairyTaskId").toString()
            )
            binding.TaskEntryTextName.setText(dairyTaskData?.dairyTaskName)
            binding.TaskEntryTextDescription.setText(dairyTaskData?.dairyTaskDescription)
        }

        getSubTasks()

        //Adapter
        adapter = SubTasksAdapter(list)
        val layManager = LinearLayoutManager(requireContext())
        layManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvSubtask.layoutManager = layManager
        binding.rvSubtask.adapter = adapter
        adapter.setListener(this)

        //Listeners
        binding.BtnTaskSave.setOnClickListener(){
            val taskName=binding.TaskEntryTextName.text.toString()
            val taskDescription=binding.TaskEntryTextDescription.text.toString()

            if(taskName!=null){
                listener?.onUpdateDairyTask(taskName, taskDescription, dairyTaskData?.dairyTaskId.toString(), time, date, binding.TaskEntryTextName, binding.TaskEntryTextDescription)
            }
        }

        binding.TimeSetter.setOnClickListener(){
            time=openTimePicker()
        }
        binding.ivAddST.setOnClickListener {
            activateSubTasks()
            status = "subcr"
        }

        submit.setOnClickListener {
            submitClicked()
        }
    }

    //submitting subtask
    private fun submitClicked(){
        Log.d("STATUS", "$status, $position")
        if(subTaskEditText.text.isNotEmpty()){
            if(status == "subcr"){
                val subtask = DairyTaskData(dairyTaskName = subTaskEditText.text.toString(), dairyTaskId = FireHelper.Users.push().key.toString(), ifSub = true)
                subTaskEditText.text.clear()

                FireHelper.Users.child(FireHelper.firebaseAuth.currentUser!!.uid).child("DairyTasks")
                    .child(dairyTaskData!!.dairyTaskId).child("SubTasks")
                    .child(subtask.dairyTaskId).setValue(subtask).addOnCompleteListener {
                    if(it.isSuccessful){
                        list.add(subtask)

                        adapter.notifyItemInserted(list.size - 1)
                    }
                }
            }
            else if(status == "subed"){
                if(list[position].dairyTaskName != subTaskEditText.text.toString() && subTaskEditText.text.trim().toString() != ""){
                    FireHelper.Users.child(FireHelper.firebaseAuth.currentUser!!.uid).child("DairyTasks").child(dairyTaskData!!.dairyTaskId)
                        .child("SubTasks").child(list[position].dairyTaskId).child(list[position].dairyTaskId)
                        .setValue(subTaskEditText.text.trim().toString()).addOnCompleteListener{

                        if(it.isSuccessful){
                            list[position].dairyTaskName = subTaskEditText.text.trim().toString()
                            adapter.notifyItemChanged(position)
                            subTaskEditText.text.clear()

                        }
                    }
                }
            }
        }
    }

    private fun activateSubTasks(){
        if(status == "subed"){
            subTaskEditText.visibility = View.VISIBLE
            submit.visibility = View.VISIBLE
            subTaskEditText.requestFocus()
            return
        }
        subTaskEditText.visibility = if(subTaskEditText.visibility == View.GONE) View.VISIBLE else View.GONE
        submit.visibility = if(submit.visibility == View.GONE) View.VISIBLE else View.GONE
        subTaskEditText.requestFocus()
    }

    override fun onDeleteClicked(task: DairyTaskData, position: Int){
        FireHelper.Users.child(FireHelper.firebaseAuth.currentUser!!.uid).child("DairyTasks").child(dairyTaskData!!.dairyTaskId).child("SubTasks").child(task.dairyTaskId).removeValue().addOnCompleteListener {
            if(it.isSuccessful){
                list.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
        }
    }

    override fun onEditClicked(task: DairyTaskData, position: Int) {
        status = "subed"
        activateSubTasks()
        subTaskEditText.setText(list[position].dairyTaskName)
        this@TaskDescriptionFragment.position = position
    }

    private fun getSubTasks(){
        Log.d("SUBTASK", "Getting")
        val path = FireHelper.Users.child(FireHelper.firebaseAuth.currentUser!!.uid).child("DairyTasks").child(dairyTaskData!!.dairyTaskId).child("SubTasks")
        path.get().addOnCompleteListener {
            for(i in it.result.children){
                val task = i.getValue(DairyTaskData::class.java)


                if(task != null){
                    Log.d("SUBTASK", task.dairyTaskName)
                    list.add(task)
                    adapter.notifyItemInserted(list.size - 1)
                }
            }
        }
    }

    private fun openTimePicker():String {
        var time=""
        val isSystem24hour= DateFormat.is24HourFormat(requireContext())
        val clockFormat =if(isSystem24hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat).setHour(12).setMinute(0).setTitleText("Set notification time").build()

        picker.show(childFragmentManager, "TAG")

        picker.addOnCancelListener(){

        }
        picker.addOnDismissListener(){

        }
        picker.addOnNegativeButtonClickListener(){

        }
        picker.addOnPositiveButtonClickListener(){
            val h=picker.hour
                .toString()
            val m=picker.minute
                .toString()
            time= "$h:$m"

        }
        return time
    }


}