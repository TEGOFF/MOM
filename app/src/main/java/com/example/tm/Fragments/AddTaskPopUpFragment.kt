package com.example.tm.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.tm.databinding.FragmentAddTaskPopUpBinding
import DataClasses.Category
import DataClasses.DairyTaskData
import ModulesAndAdapters.FireHelper
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AddTaskPopUpFragment : DialogFragment() {
    private lateinit var binding:FragmentAddTaskPopUpBinding
    private var listener:DialogBtnClickListeners?=null
    private var dairyTaskData: DairyTaskData? = null

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



    var time = ""
    var date:String=""

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rgCats.visibility = View.GONE

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
            val taskCategory = "# " + binding.btChooseCat.text.toString()
            if(taskName.isNotEmpty()){
                if(dairyTaskData==null){
                    listener?.onSaveDairyTask(
                        taskName, taskDescription , time, date, binding.TaskEntryTextName, binding.TaskEntryTextDescription, taskCategory)

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
        binding.ibCalendar.setOnClickListener {
            openDatePicker()
        }

        getCats()

        binding.rgCats.setOnCheckedChangeListener { group, checkedId ->
            val radio = view.findViewById<RadioButton>(checkedId)

            binding.btChooseCat.setText(radio.text)
            binding.rgCats.visibility = View.GONE
        }

        binding.btChooseCat.setOnClickListener {
            binding.rgCats.visibility = View.VISIBLE
        }
    }

    private fun getCats(){
        FireHelper.Users.child(FireHelper.firebaseAuth.currentUser!!.uid).child("Categories").get().addOnCompleteListener {
            if(it.isSuccessful){
                for(i in it.result.children){
                    val cat = i.getValue(Category::class.java)

                    if(cat != null){
                        val radio = RadioButton(context)
                        Log.i("Cat", cat.name)
                        radio.setText(cat.name)
                        radio.id = View.generateViewId()

                        binding.rgCats.addView(radio)
                    }
                }
            }
        }
    }

    private fun openTimePicker():String {
        val isSystem24hour=is24HourFormat(requireContext())
        val clockFormat =if(isSystem24hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat).setHour(12).setMinute(0).setTitleText("Set notification time").build()

        picker.show(childFragmentManager, "TAG")

        picker.addOnCancelListener(){

        }
        picker.addOnDismissListener(){

        }
        picker.addOnNegativeButtonClickListener(){}
        picker.addOnPositiveButtonClickListener() {
            val h=picker.hour
                .toString()

            val m=picker.minute
                .toString()

            time= "$h:$m"
        }
        return time
    }

    private fun openDatePicker(){
        val datePicker: MaterialDatePicker<Long> = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Choose date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener {
            date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(it))
        }

        datePicker.show(childFragmentManager, "TAG")
    }

    interface DialogBtnClickListeners{
        fun onSaveDairyTask(taskName:String , taskDescription:String, time:String = "Not set", date:String = "Not set", taskEntryTextDescription: TextInputEditText, taskEntryTextName:TextInputEditText, taskCategory: String = "Not set")
        fun onUpdateDairyTask(taskName:String , taskDescription:String, time:String, date:String, taskId:String ,taskEntryTextDescription: TextInputEditText, taskEntryTextName:TextInputEditText)
        fun onDeleteDairyTaskData(dairyTaskData: DairyTaskData)
    }
}