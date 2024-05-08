package com.example.tm.Fragments

import android.os.Bundle
import android.os.Parcel
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.tm.R
import com.example.tm.databinding.FragmentSignUpBinding
import DataClasses.Category
import DataClasses.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignUpFragment() : Fragment() {
    private lateinit var auth:FirebaseAuth
    private lateinit var dbref:DatabaseReference
    private lateinit var navControl: NavController
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        registerEvents()
    }

    private fun init(view:View){
        navControl= Navigation.findNavController(view)
        auth= FirebaseAuth.getInstance()
        dbref=FirebaseDatabase.getInstance().reference.child("Users")

    }

    private fun registerEvents() {
        binding.ButtonBackToLogIn.setOnClickListener{
            navControl.navigate(R.id.action_signUpFragment_to_signInFragment)
        }

        binding.ButtonSignUp.setOnClickListener {
            val email = binding.EntryEmailSignUp.text.toString()
            val name = binding.EntryNameSignUp.text.toString()
            val pass = binding.EntryPasswordSignUp.text.toString()
            val passconf = binding.EntryPassConfSignUp.text.toString()
            if (email.isNotEmpty() && pass.isNotEmpty()&& passconf.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(context, "User registered successfully", Toast.LENGTH_SHORT).show()
                        val user = User(name, email, pass, auth.uid.toString())
                        navControl.navigate(R.id.action_signUpFragment_to_homeFragment)
                        dbref.child(user.userId).setValue(user).addOnCompleteListener {
                            if(it.isSuccessful){
                                addStandartCats()
                            }
                        }
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

    //adding standard categories
    private fun addStandartCats(){
        val cats = arrayListOf(
            Category(
                id = dbref.push().key.toString(),
                name = "Home"
            ),
            Category(
                id = dbref.push().key.toString(),
                name = "Work"
            ),
            Category(
                id = dbref.push().key.toString(),
                name = "Purchases"
            ),
            Category(
                id = dbref.push().key.toString(),
                name = "Study"
            ),
            Category(
                id = dbref.push().key.toString(),
                name = "All"
            )
        )

        dbref.child(auth.currentUser!!.uid).child("Categories").child(cats[0].id).setValue(cats[0])
        dbref.child(auth.currentUser!!.uid).child("Categories").child(cats[1].id).setValue(cats[1])
        dbref.child(auth.currentUser!!.uid).child("Categories").child(cats[2].id).setValue(cats[2])
        dbref.child(auth.currentUser!!.uid).child("Categories").child(cats[3].id).setValue(cats[3])
    }



}