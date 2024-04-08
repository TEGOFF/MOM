package com.example.tm.Fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.tm.R
import com.example.tm.databinding.FragmentSplashBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.handleCoroutineException


class SplashFragment : Fragment() {

    private lateinit var auth:FirebaseAuth
    private lateinit var navControl:NavController
    private lateinit var binding : FragmentSplashBinding



    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            binding= FragmentSplashBinding.inflate(inflater, container, false)
            return return binding.root

        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            progressBar()
            navControl=Navigation.findNavController(view)
            auth=FirebaseAuth.getInstance()
            Handler(Looper.myLooper()!!).postDelayed(Runnable {
                binding.progressBar.setVisibility(View.INVISIBLE)
                if(auth.currentUser!=null){
                navControl.navigate(R.id.action_splashFragment_to_homeFragment)
                }
                else{
                navControl.navigate(R.id.action_splashFragment_to_signInFragment)
                }

        }, 2000 )


    }
    fun progressBar(){
        var progressBar:ProgressBar =binding.progressBar
        progressBar.setVisibility(View.VISIBLE)

    }
}