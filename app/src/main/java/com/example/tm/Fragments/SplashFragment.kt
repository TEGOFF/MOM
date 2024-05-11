package com.example.tm.Fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.app.AlertDialog
import android.os.Build
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
        ): View {
            // Inflate the layout for this fragment
            binding= FragmentSplashBinding.inflate(inflater, container, false)
            return binding.root

        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            progressBar()
            navControl=Navigation.findNavController(view)
            auth=FirebaseAuth.getInstance()
            Handler(Looper.myLooper()!!).postDelayed( {
                binding.progressBar.visibility = View.INVISIBLE
                if(isNetworkAvailable(requireContext())) {
                    if (auth.currentUser != null) {
                        navControl.navigate(R.id.action_splashFragment_to_homeFragment)
                    } else {
                        navControl.navigate(R.id.action_splashFragment_to_signInFragment)
                    }
                }
                else{
                        val builder = AlertDialog.Builder(requireContext())

                        builder.setTitle("Alert")

                        builder.setMessage("No internet connection. Connect your device and restart the app")
                        builder.setIcon(android.R.drawable.ic_dialog_alert)


                        builder.setPositiveButton("Close"){dialogInterface, which ->
                            activity?.finish()
                        }


                        val alertDialog: AlertDialog = builder.create()

                        alertDialog.setCancelable(false)
                        alertDialog.show()
                }
            }, 2000 )


        }
    //progress bar initialization
    private fun progressBar(){
        val progressBar:ProgressBar =binding.progressBar
        progressBar.visibility = View.VISIBLE

    }
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}