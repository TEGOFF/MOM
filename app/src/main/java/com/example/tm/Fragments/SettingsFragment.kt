package com.example.tm.Fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.tm.R
import com.example.tm.databinding.FragmentSettingsBinding
import ModulesAndAdapters.FireHelper
import DataClasses.User
import android.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage


class SettingsFragment : Fragment() , NameChangeFragment.DialogNameChangeListener{

    private lateinit var navControl: NavController
    private lateinit var mFirebase: FirebaseAuth
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var firebaseStorageRef:FirebaseStorage
    private lateinit var nameChangeFragment:NameChangeFragment
    private lateinit var me: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navControl=Navigation.findNavController(view)
        init(view)
        registerEvents()
        binding.profilePhotoCard.setOnClickListener{
            saveProfImage()
        }
        setPage()
    }

    private fun setPage() {
        val dbref= FireHelper.Users.child(FireHelper.firebaseAuth.currentUser?.uid.toString())
        dbref.get().addOnCompleteListener { //Getting the user
            if(it.isSuccessful){
                val user = it.result.getValue(User::class.java) //Converting to class User

                if(user != null && user.userId == FireHelper.firebaseAuth.currentUser?.uid){ //Checking if this user is what we expect to load to the page
                    me = user //Setting current user as "me"

                    binding.UserName.setText(me.userName) //Setting my name to the text view


                        Glide.with(this).load(me.profileImage).into(binding.profilePhoto) //Setting profile image from db into profileImage view using Glide library

                    Log.d("SettingsUser", "image: ${me.profileImage}")
                }
            }
        }

    }

    fun init(view: View){
        navControl=Navigation.findNavController(view)
        mFirebase=FirebaseAuth.getInstance()
        firebaseStorageRef=FirebaseStorage.getInstance()
    }

     private fun registerEvents(){
        binding.homePageBtn.setOnClickListener(){
            navControl.navigate(R.id.action_settingsFragment_to_homeFragment)
        }
        binding.logoutBtn.setOnClickListener() {
            mFirebase.signOut()
            navControl.navigate(R.id.action_settingsFragment_to_signInFragment)
        }
         binding.takePhotoBtn.setOnClickListener(){
            getPermissions()
            navControl.navigate(R.id.action_settingsFragment_to_cameraFragment)

         }
         binding.nameChangeBtn.setOnClickListener(){
             nameChangeFragment = NameChangeFragment()
             nameChangeFragment.setListener(this    )
             nameChangeFragment.show(
                 childFragmentManager, NameChangeFragment.TAG)

         }
         binding.applyChanges.setOnClickListener(){
             setPage()
         }
         binding.accDeleteBtn.setOnClickListener(){
             val builder= AlertDialog.Builder(context)
             builder.setTitle("Confirmation")
                 .setMessage("Are you sure you want to delete an account? All the data will also be deleted ")
                 .setPositiveButton("I`m sure"){ _, _ ->
                     FireHelper.Users.child(FireHelper.firebaseAuth.currentUser?.uid.toString()).removeValue()
                     FireHelper.firebaseAuth.currentUser?.delete()
                     FireHelper.firebaseAuth.signOut()
                     navControl.navigate(R.id.action_settingsFragment_to_signInFragment)
                 }
                 .setNegativeButton("No, take me back"){ _, _ ->
                 }.show()

         }
    }
    //permissions
    private fun getPermissions(){
        val permissionsList= mutableListOf<String>()
        context?.let {
            if (ContextCompat.checkSelfPermission(it, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                permissionsList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if(ContextCompat.checkSelfPermission(it, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                permissionsList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)

            }
            if(ContextCompat.checkSelfPermission(it, android.Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
                permissionsList.add(android.Manifest.permission.CAMERA)

            }

            if(permissionsList.size>0){
                requestPermissions(permissionsList.toTypedArray(), 101)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        grantResults.forEach {
            if(it!=PackageManager.PERMISSION_GRANTED){
                getPermissions()
            }
        }

    }

    private fun saveProfImage(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 1)

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        FireHelper.storeImage(data?.data, requireContext())
        setPage()


    }


    //name change
    override fun nameChange(newName: String) {
        FireHelper.Users.child(FireHelper.firebaseAuth.currentUser?.uid.toString()).child("userName").setValue(newName).addOnSuccessListener {
            setPage()
        }
        nameChangeFragment.dismiss()
    }
}
