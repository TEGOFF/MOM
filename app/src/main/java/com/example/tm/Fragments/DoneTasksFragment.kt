package com.example.tm.Fragments

import DataClasses.DairyTaskData
import ModulesAndAdapters.DairyTaskAdapter
import ModulesAndAdapters.FireHelper
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.tm.R
import com.example.tm.databinding.FragmentDoneTasksBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener


class DoneTasksFragment : Fragment(), DairyTaskAdapter.DairyTaskAdapterClickInterface {
    private lateinit var navControl: NavController
    private lateinit var binding: FragmentDoneTasksBinding
    private lateinit var adapter : DairyTaskAdapter
    private lateinit var mList:MutableList<DairyTaskData>
    private lateinit var dbref:DatabaseReference
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
    fun init(view:View){
        dbref= FireHelper.dbref.child("Users").child(FireHelper.firebaseAuth.currentUser?.uid.toString()).child("DairyTasks")
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        navControl= Navigation.findNavController(view)
        getDataFromFirebase()
        mList= mutableListOf()
        adapter= DairyTaskAdapter(mList)
        adapter.setListener(this)

        binding.recyclerView.adapter=adapter
        dbref.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.getValue().toString()!=""){
                    mList.add(
                        DairyTaskData(
                            snapshot.child("dairyTaskName").value.toString() ,snapshot.child("dairyTaskDescription").value.toString(), snapshot.key.toString())
                    )

                    adapter.notifyItemInserted(mList.size - 1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                if(snapshot.value.toString()!=""){
                    mList.add(
                        DairyTaskData(
                            snapshot.child("dairyTaskName").value.toString() ,snapshot.child("dairyTaskDescription").value.toString(), snapshot.key.toString())
                    )

                    adapter.notifyItemInserted(mList.size - 1)
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
        binding.recyclerView


    }
    override fun onResume() {
        super.onResume()
        getDataFromFirebase()
    }

    private fun getDataFromFirebase(){
        dbref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                mList.clear()
                adapter.notifyDataSetChanged()

                for (taskSnapshot in snapshot.children) {
                    val task = taskSnapshot.getValue(DairyTaskData::class.java)
                    if (task!= null && task.isDone)
                     {
                        mList.add(task)
                        adapter.notifyItemInserted(mList.size-1)
                    }
                }

            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onTaskClicked(taskData: DairyTaskData) {
        TODO("Not yet implemented")
    }

    override fun onEditTaskButtonClicked(taskData: DairyTaskData) {
        TODO("Not yet implemented")
    }


}