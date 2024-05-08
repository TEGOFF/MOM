package com.example.tm.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.tm.databinding.FragmentNameChangeBinding


class NameChangeFragment : DialogFragment() {

    private lateinit var listener: DialogNameChangeListener
    lateinit var binding :FragmentNameChangeBinding


    //setting a listener to DialogNameChange
    fun setListener(listener: DialogNameChangeListener) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentNameChangeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newNameAdd.setOnClickListener{
            listener.nameChange(binding.newNameEntry.text.toString())
        }
    }

    companion object{
        const val TAG ="NameChangeDialogFragment"
    }


    //interface of changing users name (extended by settingsFragment)
    interface DialogNameChangeListener{
        fun nameChange(newName:String)
    }


}