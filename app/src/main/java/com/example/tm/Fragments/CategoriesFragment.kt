package com.example.tm.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ModulesAndAdapters.CategoriesPopUpAdapter
import DataClasses.Category
import ModulesAndAdapters.FireHelper
import android.util.Log
import com.example.tm.R

class CategoriesFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //inflating a layout of this fragment
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    //initializing every UI element of categories fragment
    lateinit var rvCats: RecyclerView
    lateinit var addButton: Button
    lateinit var etCatsName: EditText

    var cats: MutableList<Category> = mutableListOf()
    lateinit var adapter: CategoriesPopUpAdapter


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
            if(name.isNotEmpty()){
                val cat = Category(FireHelper.dbref.push().key.toString(), name)
                FireHelper.Users.child(FireHelper.firebaseAuth.currentUser!!.uid).child("Categories").child(cat.id).setValue(cat)
                cats.add(cat)

                adapter.notifyItemInserted(cats.size-1)
                etCatsName.setText("")
            }
            else{
                Toast.makeText(requireContext(), "Write a name for a category", Toast.LENGTH_SHORT).show()
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
                        Log.d("catAdded", cat.toString())
                        adapter.notifyItemInserted(cats.size-1)
                    }
                }
            }
        }
    }
}