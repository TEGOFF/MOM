package com.example.tm.Fragments

import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tm.databinding.FragmentTaskDescriptionBinding
import com.example.tm.utilities.DairyTaskData
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat


class TaskDescriptionFragment : DialogFragment() {

    private var dairyTaskData:DairyTaskData?=null
    private var listener :AddTaskPopUpFragment.DialogBtnClickListeners?=null
    private lateinit var binding: FragmentTaskDescriptionBinding

    fun setListener(listener: AddTaskPopUpFragment.DialogBtnClickListeners) {
        this.listener = listener
    }
    companion object {
        const val TAG = "DialogFragment"
        @JvmStatic
        fun newInstance(task: String, taskDescription: String,  taskId: String):TaskDescriptionFragment =
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
        if (arguments != null) {
            dairyTaskData = DairyTaskData(
                arguments?.getString("dairyTaskName").toString(),
                arguments?.getString("dairyTaskDescription").toString(),
                arguments?.getString("dairyTaskId").toString()
            )
            binding.TaskEntryTextName.setText(dairyTaskData?.dairyTaskName)
            binding.TaskEntryTextDescription.setText(dairyTaskData?.dairyTaskDescription)
        }
        binding.BtnTaskSave.setOnClickListener(){
            val taskName=binding.TaskEntryTextName.text.toString()
            val taskDescription=binding.TaskEntryTextDescription.text.toString()

            if(taskName!=null){
                listener?.onUpdateDairyTask(taskName, taskDescription, dairyTaskData?.dairyTaskId.toString(), time, date, binding.TaskEntryTextName, binding.TaskEntryTextDescription)
            }
        }
        binding.deleteTask.setOnClickListener(){
            listener?.onDeleteDairyTaskData(DairyTaskData(
                arguments?.getString("dairyTaskName").toString(),
                arguments?.getString("dairyTaskDescription").toString(),
                arguments?.getString("dairyTaskId").toString()
            ))
        }
        binding.TimeSetter.setOnClickListener(){
            time=openTimePicker()
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