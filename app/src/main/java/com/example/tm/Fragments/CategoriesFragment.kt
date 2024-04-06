package com.example.tm.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ModulesAndAdapters.CategoriesPopUpAdapter
import DataClasses.Category
import ModulesAndAdapters.FireHelper
import DataClasses.User
import com.example.tm.R

class CategoriesFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }


    lateinit var rvCats: RecyclerView
    lateinit var addButton: Button
    lateinit var etCatsName: EditText

    var cats: MutableList<Category> = mutableListOf()
    lateinit var adapter: CategoriesPopUpAdapter

    var me: User = User()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view, requireContext())
    }

    fun init(view: View, context: Context){
        rvCats = view.findViewById(R.id.rvCats)
        addButton = view.findViewById(R.id.btAddCat)
        etCatsName = view.findViewById(R.id.etNewCatsName)

        adapter = CategoriesPopUpAdapter(view, cats)

        rvCats.layoutManager = LinearLayoutManager(context)
        rvCats.adapter = adapter

        initListeners()
        getCats()
    }

    fun initListeners(){
        addButton.setOnClickListener {
            val name = etCatsName.text.toString().trim()
            if(name != " "){
                val cat = Category(FireHelper.dbref.push().key.toString(), name)
                FireHelper.Users.child(FireHelper.firebaseAuth.currentUser!!.uid).child("Categories").child(cat.id).setValue(cat)
                cats.add(cat)

                adapter.notifyItemInserted(cats.size-1)
                etCatsName.setText("")
            }
        }
    }

    fun getMe(){
        FireHelper.Users.child(FireHelper.firebaseAuth.currentUser!!.uid).get().addOnCompleteListener {
            if(it.isSuccessful){
                val user = it.result.getValue(User::class.java)

                if(user != null && user.userId == FireHelper.firebaseAuth.currentUser!!.uid){
                    me = user
                }
            }
        }
    }

    fun getCats(){
        FireHelper.Users.child(FireHelper.firebaseAuth.currentUser!!.uid).child("Categories").get().addOnCompleteListener {
            if(it.isSuccessful){
                for(c in it.result.children){
                    val cat = c.getValue(Category::class.java)

                    if(cat != null){
                        cats.add(cat)

                        adapter.notifyItemInserted(cats.size-1)
                    }
                }
            }
        }
    }
}