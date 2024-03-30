package com.example.tm.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.tm.R
import com.example.tm.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth


class SettingsFragment : Fragment() {

    private lateinit var navControl: NavController
    private lateinit var mFirebase: FirebaseAuth
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navControl=Navigation.findNavController(view)
        init(view)
        registerEvents()

    }
    fun init(view: View){
        navControl=Navigation.findNavController(view)
        mFirebase=FirebaseAuth.getInstance()
    }

     fun registerEvents(){
        binding.homePageBtn.setOnClickListener(){
            navControl.navigate(R.id.action_settingsFragment_to_homeFragment)
        }
        binding.logoutBtn.setOnClickListener(){
            mFirebase.signOut()
            navControl.navigate(R.id.action_settingsFragment_to_signInFragment)
        }


    }




}