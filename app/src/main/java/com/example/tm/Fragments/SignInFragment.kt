package com.example.tm.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.tm.MainActivity
import com.example.tm.R
import com.example.tm.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth


class SignInFragment : Fragment()
{
    private lateinit var auth:FirebaseAuth
    private lateinit var nav_control:NavController
    private lateinit var binding:FragmentSignInBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvents()
    }
    private fun init(view:View){
        nav_control=Navigation.findNavController(view)
        auth=FirebaseAuth.getInstance()

    }
    private fun registerEvents(){

        binding.buttonRegPage.setOnClickListener{
            nav_control.navigate(R.id.action_signInFragment_to_signUpFragment)
        }
        binding.buttonSignIn.setOnClickListener{
            val email=binding.entryEmailSignIn.text.toString()
            val pass=binding.entryPasswordSignIn.text.toString()
            if(email.isNotEmpty()&&pass.isNotEmpty()){
                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener{
                    if(it.isSuccessful) {

                        nav_control.navigate(R.id.action_signInFragment_to_homeFragment)
                    }
                    else{
                        Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }

            }
            else{
                Toast.makeText(context, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }

    }
}

