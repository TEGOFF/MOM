package com.example.tm.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tm.R
import com.example.tm.databinding.FragmentHomeBinding
import com.example.tm.utilities.DairyTaskAdapter
import com.example.tm.utilities.DairyTaskData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.example.tm.Fragments.AddTaskPopUpFragment.Companion as AddTaskPopUpFragment1
import com.example.tm.Fragments.TaskDescriptionFragment.Companion as TaskDescriptionFragment1



class HomeFragment : Fragment(), AddTaskPopUpFragment.DialogBtnClickListeners,
    DairyTaskAdapter.DairyTaskAdapterClickInterface {

    private lateinit var auth:FirebaseAuth
    private lateinit var navControl:NavController
    private lateinit var binding:FragmentHomeBinding
    private lateinit var dbref:DatabaseReference
    private  var addPopUpFragment: AddTaskPopUpFragment?=null
    private  var taskPopUpFragment:TaskDescriptionFragment?=null
    private lateinit var adapter:DairyTaskAdapter
    private lateinit var mlist:MutableList<DairyTaskData>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        var drawerlayout: DrawerLayout? = view?.findViewById(R.id.)
            {
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        init(view)
        registerEvents()


    }
    private fun init(view:View){
        navControl=Navigation.findNavController(view)
        auth=FirebaseAuth.getInstance()
        dbref=FirebaseDatabase.getInstance().reference
            .child(auth.currentUser?.uid.toString()).child("DairyTasks")
        binding.mainRecyclerView.setHasFixedSize(true)
        getDataFromFirebase()
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(context)
        mlist= mutableListOf()
        adapter= DairyTaskAdapter(mlist)
        adapter.setListener(this)
        binding.mainRecyclerView.adapter=adapter
        dbref.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.getValue().toString()!=""){
                    mlist.add(DairyTaskData(
                        snapshot.child("dairyTaskName").value.toString() ,snapshot.child("dairyTaskDescription").value.toString(), snapshot.key.toString()))

                    adapter.notifyItemInserted(mlist.size - 1)
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.getValue().toString()!=""){

                    adapter.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                if(snapshot.getValue().toString()!=""){
                    val index=mlist.indexOf(DairyTaskData(
                        snapshot.child(
                            "dairyTaskName").getValue().toString(), snapshot.child("dairyTaskDescription").getValue().toString(), snapshot.key.toString()))
                    mlist.remove(DairyTaskData(
                        snapshot.child(
                            "dairyTaskName").getValue().toString(), snapshot.child("dairyTaskDescription").getValue().toString() , snapshot.key.toString()))
                    adapter.notifyItemRemoved(index)

                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("error", error.toString())
            }

        })
    }
    private fun registerEvents(){
        if(addPopUpFragment!= null){
            childFragmentManager.beginTransaction().remove(addPopUpFragment!!).commit()
        }

        binding.ButtonSignOut.setOnClickListener(){
            auth.signOut()
            navControl.navigate(R.id.action_homeFragment_to_signInFragment)
        }
        binding.AddTaskButton.setOnClickListener{
            addPopUpFragment=AddTaskPopUpFragment()
            addPopUpFragment!!.setListener(this)
            addPopUpFragment!!.show(
                childFragmentManager, AddTaskPopUpFragment1.TAG
            )
        }
        binding.mainRecyclerView.setOnClickListener(){
            taskPopUpFragment= TaskDescriptionFragment()
            taskPopUpFragment!!.setListener(this)
            taskPopUpFragment!!.show(
                childFragmentManager, TaskDescriptionFragment.TAG
            )
        }
    }
    private fun getDataFromFirebase(){
        dbref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                mlist.clear()
                for (taskSnapshot in snapshot.children) {
                    val task = taskSnapshot.getValue(DairyTaskData::class.java)
                    if ( task!= null) {
                        mlist.add(task)
                    }
                }



                }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()


            }

        })
    }





    override fun onDeleteDairyTaskData(dairyTaskData: DairyTaskData) {
        if(taskPopUpFragment !=null){
            childFragmentManager.beginTransaction().remove(taskPopUpFragment!!).commit()
        }

        dbref.child(dairyTaskData.dairyTaskId).removeValue().addOnCompleteListener(){
            if(it.isSuccessful.not()){
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT    ).show()
            }

        }
        taskPopUpFragment!!.dismiss()
    }



    override fun onTaskClicked(dairyTaskData: DairyTaskData) {
        if(taskPopUpFragment!=null)
            childFragmentManager.beginTransaction().remove(taskPopUpFragment!!).commit()

        taskPopUpFragment=TaskDescriptionFragment.newInstance(dairyTaskData.dairyTaskName, dairyTaskData.dairyTaskDescription, dairyTaskData.dairyTaskId)
        taskPopUpFragment!!.setListener(this)
        taskPopUpFragment!!.show(
            childFragmentManager,
            TaskDescriptionFragment.TAG

        )
        FirebaseMessaging.getInstance().getToken()
    }

    override fun onEditTaskButtonClicked(dairyTaskData: DairyTaskData) {
        if (taskPopUpFragment != null)
            childFragmentManager.beginTransaction().remove(taskPopUpFragment!!).commit()

        taskPopUpFragment = TaskDescriptionFragment.newInstance(dairyTaskData.dairyTaskName, dairyTaskData.dairyTaskDescription, dairyTaskData.dairyTaskId)
        taskPopUpFragment!!.setListener(this)
        taskPopUpFragment!!.show(
            childFragmentManager,
            TaskDescriptionFragment.TAG
        )

    }



    override fun onSaveDairyTask(taskName:String, taskDescription:String , time:String, date:String, taskDescriptionEntryText: TextInputEditText,   taskNameEntryText: TextInputEditText) {
        val k=dbref.push()
        k.setValue(DairyTaskData(taskName, taskDescription, k.key.toString(), time, date )).addOnCompleteListener{
            if(it.isSuccessful){
                Toast.makeText(context, "Task added succesfully", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }

            }
            taskDescriptionEntryText.text=null
            taskNameEntryText.text=null
            addPopUpFragment!!.dismiss()

        }

     override fun onUpdateDairyTask(taskName:String, taskDescription:String , taskId:String, time:String, date:String, taskEntryTextName: TextInputEditText, taskEntryTextDescription:TextInputEditText) {
        val map = mapOf<String, Any>(
            "dairyTaskDescription" to taskDescription,
            "dairyTaskId" to taskId,
            "dairyTaskName" to taskName,
            "dairyTaskNotifyTime" to time,
            "dairyTaskDate" to date)
        dbref.child(taskId).updateChildren(map).addOnCompleteListener() {
            if (it.isSuccessful)
                Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show()
            else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            taskEntryTextName.text = null
            taskEntryTextDescription.text=null

            addPopUpFragment!!.dismiss()

            taskPopUpFragment!!.dismiss()
        }
    }


}






