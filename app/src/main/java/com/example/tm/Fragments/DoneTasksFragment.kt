package com.example.tm.Fragments

import DataClasses.DairyTaskData
import ModulesAndAdapters.DairyTaskAdapter
import ModulesAndAdapters.FireHelper
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tm.R
import com.example.tm.databinding.FragmentDoneTasksBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener


class DoneTasksFragment : Fragment(), DairyTaskAdapter.DairyTaskAdapterClickInterface,
    AddTaskPopUpFragment.DialogBtnClickListeners {
    private lateinit var navControl: NavController
    private lateinit var binding: FragmentDoneTasksBinding
    private lateinit var adapter : DairyTaskAdapter
    private lateinit var mlist:MutableList<DairyTaskData>
    private lateinit var dbref:DatabaseReference
    private var taskPopUpFragment:TaskDescriptionFragment? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDoneTasksBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        registerEvents(view)
    }
    private fun init(view:View){
        dbref= FireHelper.dbref.child("Users").child(FireHelper.firebaseAuth.currentUser?.uid.toString()).child("DairyTasks")
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        navControl= Navigation.findNavController(view)

        getDataFromFirebase()
        mlist= mutableListOf()
        adapter= DairyTaskAdapter(mlist)
        adapter.setListener(this)

        binding.recyclerView.adapter=adapter
        dbref.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.getValue().toString()!=""){
                    mlist.add(
                        DairyTaskData(
                            snapshot.child("dairyTaskName").value.toString() ,snapshot.child("dairyTaskDescription").value.toString(), snapshot.key.toString())
                    )

                    adapter.notifyItemInserted(mlist.size - 1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                if(snapshot.value.toString()!=""){
                    mlist.add(
                        DairyTaskData(
                            snapshot.child("dairyTaskName").value.toString() ,snapshot.child("dairyTaskDescription").value.toString(), snapshot.key.toString())
                    )

                    adapter.notifyItemInserted(mlist.size - 1)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun registerEvents(view: View){
        binding.BackBtn.setOnClickListener(){
            navControl.navigate(R.id.action_doneTasksFragment_to_homeFragment)
        }

        binding.recyclerView.setOnClickListener(){
            taskPopUpFragment= TaskDescriptionFragment()
            taskPopUpFragment!!.setListener(this)
            taskPopUpFragment!!.show(
                childFragmentManager, TaskDescriptionFragment.TAG
            )
        }


    }
    override fun onResume() {
        super.onResume()
        getDataFromFirebase()
    }

    private fun getDataFromFirebase(){
        dbref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                mlist.clear()
                adapter.notifyDataSetChanged()

                for (taskSnapshot in snapshot.children) {
                    val task = taskSnapshot.getValue(DairyTaskData::class.java)
                    if ( task!= null&& task.isDone) {
                        if(taskSnapshot.hasChild("SubTasks")){
                            task.containsSub = true
                        }
                        mlist.add(task)
                        adapter.notifyItemInserted(mlist.size-1)
                    }
                }

            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onTaskClicked(dairyTaskData: DairyTaskData) {
        if (taskPopUpFragment != null)
            childFragmentManager.beginTransaction().remove(taskPopUpFragment!!).commit()

        taskPopUpFragment = TaskDescriptionFragment.newInstance(
            dairyTaskData.dairyTaskName,
            dairyTaskData.dairyTaskDescription,
            dairyTaskData.dairyTaskId
        )
        taskPopUpFragment!!.setListener(this)
        taskPopUpFragment!!.show(
            childFragmentManager,
            TaskDescriptionFragment.TAG
        )
    }



    override fun onDeleteTaskClicked(dairyTaskData: DairyTaskData) {
        dbref.child(dairyTaskData.dairyTaskId).removeValue().addOnCompleteListener(){
            if(it.isSuccessful.not()){
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT    ).show()
            }
        }
    }

    override fun onCheckBoxClicked(taskData: DairyTaskData, position: Int) {
        val builder= AlertDialog.Builder(context)
        builder.setTitle("Confirmation")
            .setMessage("Are you sure you want to recreate this task?")
            .setPositiveButton("I`m sure"){ _, _ ->
                taskData.isDone=false
                FireHelper.Users.child(FireHelper.firebaseAuth.currentUser?.uid.toString()).child("DairyTasks")
                    .child(taskData.dairyTaskId).child("done").setValue(false)

                mlist.add(position, mlist.removeAt(position) )

                view?.post(){
                    binding.recyclerView.adapter?.notifyDataSetChanged()
                }
            }
            .setNegativeButton("No"){ _, _ ->
            }.show()



    }

    override fun onSaveDairyTask(
        taskName: String,
        taskDescription: String,
        time: String,
        date: String,
        taskEntryTextDescription: TextInputEditText,
        taskEntryTextName: TextInputEditText,
        taskCategory: String
    ) {
        //doesn`t need to be implemented because not in use
    }

    override fun onUpdateDairyTask(
        taskName: String,
        taskDescription: String,
        time: String,
        date: String,
        taskId: String,
        taskEntryTextDescription: TextInputEditText,
        taskEntryTextName: TextInputEditText
    ) {
        //doesn`t need to be implemented because not in use
    }


}