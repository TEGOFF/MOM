package com.example.tm.utilities

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.tm.Fragments.SettingsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FireHelper {
    companion object {
        var firebaseAuth: FirebaseAuth =FirebaseAuth.getInstance()
        lateinit var firebaseStorage: FirebaseStorage
        var stref:StorageReference =FirebaseStorage.getInstance().reference
        var dbref:DatabaseReference = FirebaseDatabase.getInstance().reference
        lateinit var adapter:DairyTaskAdapter
        val Users = FirebaseDatabase.getInstance().getReference("Users")


        fun storeImage(uri: Uri?,  context: Context) {
            if(uri != null){
                val storage = stref.child("${firebaseAuth.currentUser?.uid}.profileImage.${getType(context, uri!!)}")

                val uploadTask = storage.putFile(uri!!).continueWithTask { task ->
                    if(!task.isSuccessful){
                        task.exception.let { throw it!! }
                    }

                    return@continueWithTask storage.downloadUrl
                }.addOnCompleteListener {
                    val link = it.result.toString()

                    Log.d("LINK", link)

                    Users.child(firebaseAuth.currentUser?.uid.toString()).child("profileImage").setValue(link).addOnCompleteListener {
                        if(it.isSuccessful){
                            SettingsFragment()
                        }
                    }
                }


            }

        }
        fun getType(context: Context, uri: Uri) : String{
            val contentResolver: ContentResolver = context.contentResolver
            return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri)).toString()
        }




    }




}