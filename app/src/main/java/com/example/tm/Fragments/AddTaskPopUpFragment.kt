package com.example.tm.Fragments

import android.annotation.SuppressLint
import android.app.ActivityManager.TaskDescription
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.tm.R
import com.example.tm.databinding.FragmentAddTaskPopUpBinding
import com.example.tm.utilities.DairyTaskData
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat



class AddTaskPopUpFragment : DialogFragment() {
    private lateinit var binding:FragmentAddTaskPopUpBinding
    private var listener:DialogBtnClickListeners?=null
    private var dairyTaskData:DairyTaskData? = null

    fun setListener(listener: DialogBtnClickListeners) {
        this.listener = listener
    }

    companion object {
        const val TAG = "DialogFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentAddTaskPopUpBinding.inflate(inflater, container, false)
        return binding.root


    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var time:String=""
        var date:String=""

        if(arguments !=null){
            dairyTaskData= DairyTaskData(
                arguments?.getString("dairyTaskName").toString(),
                arguments?.getString("dairyTaskDescription").toString(),
                arguments?.getString("dairyTaskId").toString(),
                arguments?.getString("notificationTime").toString(),
                arguments?.getString("date").toString())
                binding.TaskEntryTextName.setText(dairyTaskData?.dairyTaskName)


        }

        binding.BtnTaskAdd.setOnClickListener{
            val taskName=binding.TaskEntryTextName.text.toString()
            val taskDescription=binding.TaskEntryTextDescription.text.toString()
            if(taskName.isNotEmpty()){
                if(dairyTaskData==null){
                    listener?.onSaveDairyTask(
                        taskName, taskDescription , time, date, binding.TaskEntryTextName, binding.TaskEntryTextDescription  )

                }
                else{
                    listener?.onUpdateDairyTask(
                        taskName, taskDescription, dairyTaskData?.dairyTaskId.toString(), time, date, binding.TaskEntryTextName, binding.TaskEntryTextDescription)
                }
            }
            else{
                Toast.makeText(context, "Please type something", Toast.LENGTH_SHORT).show()
            }

        }
        binding.TimerSetter.setOnClickListener(){
            time=openTimePicker()

        }


    }

    private fun openTimePicker():String {
        var time=""
        val isSystem24hour=is24HourFormat(requireContext())
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


    interface DialogBtnClickListeners{
        fun onSaveDairyTask(taskName:String , taskDescription:String, time:String, date:String, taskEntryTextDescription: TextInputEditText, taskEntryTextName:TextInputEditText)
        fun onUpdateDairyTask(taskName:String , taskDescription:String, time:String, date:String, taskId:String ,taskEntryTextDescription: TextInputEditText, taskEntryTextName:TextInputEditText)
        fun onDeleteDairyTaskData(dairyTaskData: DairyTaskData)
    }


}