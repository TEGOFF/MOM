package com.example.tm.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tm.R
import com.example.tm.databinding.FragmentHomeBinding
import DataClasses.Category
import ModulesAndAdapters.DairyTaskAdapter
import DataClasses.DairyTaskData
import ModulesAndAdapters.FireHelper
import androidx.core.view.GravityCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.example.tm.Fragments.AddTaskPopUpFragment.Companion as AddTaskPopUpFragment1


class HomeFragment : Fragment(), AddTaskPopUpFragment.DialogBtnClickListeners,
    DairyTaskAdapter.DairyTaskAdapterClickInterface, View.OnClickListener {

    private lateinit var auth:FirebaseAuth
    private lateinit var navControl:NavController
    private lateinit var binding:FragmentHomeBinding
    private lateinit var dbref:DatabaseReference
    private  var addPopUpFragment: AddTaskPopUpFragment?=null
    private  var taskPopUpFragment:TaskDescriptionFragment?=null
    private lateinit var adapter: DairyTaskAdapter
    private lateinit var mlist:MutableList<DairyTaskData>
    private  lateinit var actionBarToggle:ActionBarDrawerToggle
    private var category: String = "# All"
    private var date:String=""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,

        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        init(view)
        registerEvents()

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }

        binding.apply {
            actionBarToggle = ActionBarDrawerToggle(requireActivity(), drawerlayout, 0, 0)
            drawerlayout.addDrawerListener(actionBarToggle)
            actionBarToggle.syncState()

            callmenubtn.setOnClickListener(this@HomeFragment)

            navView.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.nav_home -> {
                        date = ""
                        category = ""
                        drawerlayout.closeDrawer(GravityCompat.START)
                        onResume()
                        true
                    }

                    R.id.nav_settings -> {
                        navControl.navigate(R.id.action_homeFragment_to_settingsFragment)
                        true
                    }

                    R.id.Categories -> {
                        val fragment = CategoriesFragment()

                        fragment.show(
                            childFragmentManager, "Categories"
                        )
                        true
                    }
                    R.id.nav_done_list ->{
                        navControl.navigate(R.id.action_homeFragment_to_doneTasksFragment)
                        true
                    }

                    R.id.Today -> {
                        sortByDate("Today")
                        drawerlayout.closeDrawer(GravityCompat.START)

                        true
                    }

                    R.id.Tomorrow -> {
                        sortByDate("Tomorrow")
                        drawerlayout.closeDrawer(GravityCompat.START)

                        true
                    }

                    R.id.ThisWeek -> {
                        sortByDate("ThisWeek")
                        drawerlayout.closeDrawer(GravityCompat.START)
                        true
                    }

                    R.id.AllTheTasks -> {
                        sortByDate("All the tasks")
                        drawerlayout.closeDrawer(GravityCompat.START)
                        onResume()
                        true
                    }

                    else -> {
                        false
                    }
                }
            }

        }
    }

    //Functions buy date

    //sorting
    private fun sortByDate(d: String){
        mlist.clear()

        Log.d("CAT", category)
        Log.d("Sort parameter", d)
        FireHelper.Users.child(FireHelper.firebaseAuth.currentUser!!.uid).child("DairyTasks").get().addOnCompleteListener {
            for(i in it.result.children){
                val task = i.getValue(DairyTaskData::class.java)

                if(task != null && task.date != "Not set"){
                    Log.d("CAT", "CAT IS EMPTY? ${category.isEmpty()}")
                    if(category.isEmpty()) {
                        when (d) {
                            "Today" -> {
                                val dateFormat = SimpleDateFormat(
                                    "EEE MMM dd HH:mm:ss zzz yyyy",
                                    Locale.getDefault()
                                )

                                date = "Today"

                                val date = Calendar.getInstance()
                                date.set(Calendar.HOUR_OF_DAY, 0)
                                date.set(Calendar.MINUTE, 0)
                                date.set(Calendar.SECOND, 0)

                                try {
                                    if (task.date.isNotEmpty()) {
                                        val taskDate = dateFormat.parse(task.date)
                                        Log.d("DATE", "$taskDate today: ${date.time}")
                                        if (taskDate.toString() == date.time.toString()) {
                                            mlist.add(task)
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("DATE", e.message.toString())
                                }
                            }

                            "Tomorrow" -> {
                                date = "Tomorrow"

                                val dateFormat = SimpleDateFormat(
                                    "EEE MMM dd HH:mm:ss zzz yyyy",
                                    Locale.getDefault()
                                )
                                val date = Calendar.getInstance()
                                date.add(Calendar.DAY_OF_YEAR, 1)
                                date.set(Calendar.HOUR_OF_DAY, 0)
                                date.set(Calendar.MINUTE, 0)
                                date.set(Calendar.SECOND, 0)

                                val tomorrowDate = date.time

                                try {
                                    val taskDate = dateFormat.parse(task.date)

                                    if (taskDate.toString() == tomorrowDate.toString()) {
                                        mlist.add(task)
                                    }
                                } catch (e: Exception) {
                                }
                            }

                            "ThisWeek" -> {
                                date = "ThisWeek"

                                if (isEventThisWeek(task.date)) {
                                    mlist.add(task)
                                }
                            }

                            "All the tasks" -> {
                                date = "All the tasks"

                                mlist.add(task)
                            }
                        }
                    }
                    else{
                        Log.d("CAT", "CAT: $category DATE: $date")
                        when (date) {
                            "Today" -> {
                                date = "Today"

                                val task = i.getValue(DairyTaskData::class.java)

                                val dateFormat = SimpleDateFormat(
                                    "EEE MMM dd HH:mm:ss zzz yyyy",
                                    Locale.getDefault()
                                )

                                val date = Calendar.getInstance()
                                date.set(Calendar.HOUR_OF_DAY, 0)
                                date.set(Calendar.MINUTE, 0)
                                date.set(Calendar.SECOND, 0)

                                try {
                                    if (task != null) {
                                        if (task.date.isNotEmpty()) {
                                            val taskDate = dateFormat.parse(task.date)
                                            Log.d("DATE", "$taskDate today: ${date.time}")
                                            if (taskDate.toString() == date.time.toString() && task.category == category) {
                                                mlist.add(task)
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("DATE", e.message.toString())
                                }
                            }

                            "Tomorrow" -> {
                                date = "Tomorrow"

                                val task = i.getValue(DairyTaskData::class.java)

                                val dateFormat = SimpleDateFormat(
                                    "EEE MMM dd HH:mm:ss zzz yyyy",
                                    Locale.getDefault()
                                )
                                val date = Calendar.getInstance()
                                date.add(Calendar.DAY_OF_YEAR, 1)
                                date.set(Calendar.HOUR_OF_DAY, 0)
                                date.set(Calendar.MINUTE, 0)
                                date.set(Calendar.SECOND, 0)

                                val tomorrowDate = date.time

                                try {
                                    val taskDate = dateFormat.parse(task!!.date)

                                    if (taskDate.toString() == tomorrowDate.toString() && task.category == category) {
                                        mlist.add(task)
                                    }
                                } catch (e: Exception) {
                                }
                            }

                            "ThisWeek" -> {
                                date = "ThisWeek"

                                val task = i.getValue(DairyTaskData::class.java)

                                if (task != null) {
                                    if (isEventThisWeek(task.date) && task.category == category) {
                                        Log.d("CAT", task.category)
                                        mlist.add(task)
                                    }
                                }
                            }

                            "All the tasks" -> {
                                date = "All the tasks"

                                val task = i.getValue(DairyTaskData::class.java)

                                if (task != null && task.category == category) {
                                    mlist.add(task)
                                }
                            }
                        }
                    }
                }
            }
            adapter.notifyDataSetChanged()
        }
    }

    //checking if the task is planned on thia week
    private fun isEventThisWeek(eventDate: String): Boolean {
        val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault())

        val calendar = Calendar.getInstance()

        val currentWeekStart = calendar.firstDayOfWeek
        calendar.set(Calendar.DAY_OF_WEEK, currentWeekStart)
        val startOfWeek = calendar.time

        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endOfWeek = calendar.time

        if(eventDate.isNotEmpty()){
            val eventDateTime = dateFormat.parse(eventDate)

            if (eventDateTime != null) {
                return eventDateTime.time in startOfWeek.time..endOfWeek.time
            }
        }
        return false
    }


    override fun onResume() {
        super.onResume()
        getDataFromFirebase()
    }

    //initializtion of every lateinit parameter and eventListener every task
    private fun init(view:View){
        navControl=Navigation.findNavController(view)
        auth= FireHelper.firebaseAuth
        dbref= FireHelper.dbref.child("Users").child(auth.currentUser?.uid.toString()).child("DairyTasks")



        binding.mainRecyclerView.setHasFixedSize(true)
        getDataFromFirebase()
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(context)
        mlist= mutableListOf()
        adapter= DairyTaskAdapter(mlist)
        adapter.setListener(this)
        binding.mainRecyclerView.adapter=adapter
        dbref.addChildEventListener(object :ChildEventListener{
            //adding a new task
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.value.toString()!=""){
                    mlist.add(
                        DairyTaskData(
                        snapshot.child("dairyTaskName").value.toString() ,snapshot.child("dairyTaskDescription").value.toString(), snapshot.key.toString())
                    )

                    adapter.notifyItemInserted(mlist.size - 1)
                }

            }
            //changing task
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.value.toString()!=""){
                    adapter.notifyDataSetChanged()
                }
            }
            //removing task
            override fun onChildRemoved(snapshot: DataSnapshot) {
                if(snapshot.value.toString()!=""){
                    val index=mlist.indexOf(
                        DairyTaskData(
                        snapshot.child(
                            "dairyTaskName").value.toString(), snapshot.child("dairyTaskDescription").value.toString(), snapshot.key.toString())
                    )
                    mlist.remove(
                        DairyTaskData(
                        snapshot.child(
                            "dairyTaskName").value.toString(), snapshot.child("dairyTaskDescription").value.toString() , snapshot.key.toString())
                    )
                    adapter.notifyItemRemoved(index)

                }
            }
            //doesn`t need to be implemented
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            //writing an error to log
            override fun onCancelled(error: DatabaseError) {
                Log.e("error", error.toString())
            }

        })
    }

    //registration of events happening on home screen
    private fun registerEvents(){
        if(addPopUpFragment!= null){
            childFragmentManager.beginTransaction().remove(addPopUpFragment!!).commit()
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

        getCats()

        binding.rgCats.setOnCheckedChangeListener { group, checkedId ->
            val button = requireView().findViewById<RadioButton>(checkedId)
            binding.btCat.text = button.text.toString()
            category = "# " + button.text.toString()
            binding.rgCats.visibility = View.GONE

            sortByCats()
        }

        binding.btCat.setOnClickListener {
            binding.rgCats.visibility = if(binding.rgCats.visibility == View.GONE) View.VISIBLE else View.GONE
        }
    }

    //sorting by categories
    private fun sortByCats() {

        Log.d("CAT", date)
        Log.d("CAT", category)

        mlist.clear()
        FireHelper.Users.child(FireHelper.firebaseAuth.currentUser!!.uid).child("DairyTasks").get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (date.isEmpty()) {
                        for (i in it.result.children) {
                            val task = i.getValue(DairyTaskData::class.java)

                            if (task != null && task.category == category) {
                                mlist.add(task)
                            }
                        }
                    }
                    else {
                        for (i in it.result.children) {
                            when (date) {
                                "Today" -> {
                                    val task = i.getValue(DairyTaskData::class.java)

                                    val dateFormat = SimpleDateFormat(
                                        "EEE MMM dd HH:mm:ss zzz yyyy",
                                        Locale.getDefault()
                                    )

                                    val date = Calendar.getInstance()
                                    date.set(Calendar.HOUR_OF_DAY, 0)
                                    date.set(Calendar.MINUTE, 0)
                                    date.set(Calendar.SECOND, 0)

                                    try {
                                        if (task != null) {
                                            if (task.date.isNotEmpty()) {
                                                val taskDate = dateFormat.parse(task.date)
                                                Log.d("CAT", "$taskDate today: ${date.time} ${task.category}")
                                                if (taskDate.toString() == date.time.toString() && task.category == category) {
                                                    mlist.add(task)
                                                    adapter.notifyItemInserted(mlist.size - 1)
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Log.e("DATE", e.message.toString())
                                    }
                                }

                                "Tomorrow" -> {
                                    val task = i.getValue(DairyTaskData::class.java)

                                    val dateFormat = SimpleDateFormat(
                                        "EEE MMM dd HH:mm:ss zzz yyyy",
                                        Locale.getDefault()
                                    )
                                    val date = Calendar.getInstance()
                                    date.add(Calendar.DAY_OF_YEAR, 1)
                                    date.set(Calendar.HOUR_OF_DAY, 0)
                                    date.set(Calendar.MINUTE, 0)
                                    date.set(Calendar.SECOND, 0)

                                    val tomorrowDate = date.time

                                    try {
                                        val taskDate = dateFormat.parse(task!!.date)

                                        if (taskDate != null) {
                                            if (taskDate.toString() == tomorrowDate.toString() && task.category == category) {
                                                mlist.add(task)
                                                adapter.notifyItemInserted(mlist.size - 1)
                                            }
                                        }
                                    }
                                    catch (e: Exception) {
                                        Log.e("ERROR", e.toString())
                                    }
                                }

                                "ThisWeek" -> {
                                    val task = i.getValue(DairyTaskData::class.java)

                                    if (task != null) {
                                        if (isEventThisWeek(task.date) && task.category == category) {
                                            mlist.add(task)
                                        }
                                    }
                                }

                                "All the tasks" -> {
                                    val task = i.getValue(DairyTaskData::class.java)

                                    if (task != null && task.category == category) {
                                        mlist.add(task)
                                    }
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }

    //showing categories choose menu
    private fun getCats(){
        val radio = RadioButton(context)
        radio.text = "All"
        radio.id = View.generateViewId()

        binding.rgCats.addView(radio)
        FireHelper.Users.child(FireHelper.firebaseAuth.currentUser!!.uid).child("Categories").get().addOnCompleteListener {
            if(it.isSuccessful){
                for(i in it.result.children){
                    val cat = i.getValue(Category::class.java)

                    if(cat != null){
                        val radio = RadioButton(context)
                        Log.i("Cat", cat.name)
                        radio.text = cat.name
                        radio.id = View.generateViewId()

                        binding.rgCats.addView(radio)
                    }
                }
            }
        }
    }

    //getting data from firebase and notifying adapter
    private fun getDataFromFirebase(){
        dbref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                mlist.clear()
                adapter.notifyDataSetChanged()
                for (taskSnapshot in snapshot.children) {
                    try{
                        val task = taskSnapshot.getValue(DairyTaskData::class.java)
                        if ( task!= null&& !task.isDone) {
                            if(taskSnapshot.hasChild("SubTasks")){
                                task.containsSub = true
                            }
                            mlist.add(task)
                            adapter.notifyItemInserted(mlist.size-1)
                        }
                    }catch(_:Exception){}

                }

                }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    //task is clicked
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

    //deleting dairy task
    override fun onDeleteTaskClicked(dairyTaskData: DairyTaskData) {
        dbref.child(dairyTaskData.dairyTaskId).removeValue().addOnCompleteListener(){
            if(it.isSuccessful.not()){
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT    ).show()
            }
        }
    }

    override fun onCheckBoxClicked(taskData: DairyTaskData, position: Int) {
        taskData.isDone = true
        FireHelper.Users.child(FireHelper.firebaseAuth.currentUser?.uid.toString())
            .child("DairyTasks")
            .child(taskData.dairyTaskId).child("done").setValue(true)

        mlist.add(position, mlist.removeAt(position))

        view?.post() {
            binding.mainRecyclerView.adapter?.notifyDataSetChanged()
        }
    }
    //saving dairy task
    override fun onSaveDairyTask(taskName:String, taskDescription:String , time:String, date:String, taskDescriptionEntryText: TextInputEditText,   taskNameEntryText: TextInputEditText, taskCategory: String) {
        val k=dbref.push()
        k.setValue(DairyTaskData(taskName, taskDescription, k.key.toString(), time, date, category = taskCategory)).addOnCompleteListener{
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

    //updating dairy task
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

            //checking which one of popupfragments was in use to avoid fatal errors
            if(addPopUpFragment!=null){
                addPopUpFragment!!.dismiss()
            }
            if(taskPopUpFragment!=null){
                taskPopUpFragment!!.dismiss()
            }

        }
    }


    //opening drawer layout(menu)
    override fun onClick( view: View?) {
        when (view?.id) {
            R.id.callmenubtn -> {
                binding.drawerlayout.openDrawer(binding.navView)
            }
        }
    }
}














