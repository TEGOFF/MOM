package com.example.tm.Fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.tm.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay


class SplashFragment : Fragment() {

    private lateinit var auth:FirebaseAuth
    private lateinit var navControl:NavController

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_splash, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            navControl=Navigation.findNavController(view)
            auth=FirebaseAuth.getInstance()
            Handler(Looper.myLooper()!!).postDelayed(Runnable {
                if(auth.currentUser!=null){
                navControl.navigate(R.id.action_splashFragment_to_homeFragment)
                }
                else{
                navControl.navigate(R.id.action_splashFragment_to_signInFragment)
                }

        }, 2000 )

    }
}