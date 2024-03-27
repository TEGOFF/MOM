package com.example.tm.Fragments

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.tm.R
import com.example.tm.databinding.FragmentSignInBinding
import com.example.tm.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth


class SignUpFragment() : Fragment() {
    private lateinit var auth:FirebaseAuth
    private lateinit var navControl: NavController
    private lateinit var binding: FragmentSignUpBinding

    constructor(parcel: Parcel) : this() {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvents()
    }



    private fun registerEvents() {
        binding.ButtonBackToLogIn.setOnClickListener{
            navControl.navigate(R.id.action_signUpFragment_to_signInFragment)
        }

        binding.ButtonSignUp.setOnClickListener {
            val email = binding.EntryEmailSignUp.text.toString()
            val pass = binding.EntryPasswordSignUp.text.toString()
            val passconf = binding.EntryPassConfSignUp.text.toString()
            if (email.isNotEmpty() && pass.isNotEmpty()&& passconf.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(context, "User registered successfully", Toast.LENGTH_SHORT).show()
                        navControl.navigate(R.id.action_signUpFragment_to_signInFragment)
                    }
                    else{


                        Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                Toast.makeText(context, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun init(view:View){
        navControl= Navigation.findNavController(view)
        auth= FirebaseAuth.getInstance()

    }

}