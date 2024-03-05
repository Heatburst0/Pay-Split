package com.example.paysplit.firebase

import android.util.Log
import android.widget.Toast
import com.example.paysplit.ActivityRegister
import com.example.paysplit.CreateActivity
import com.example.paysplit.models.User
import com.example.paysplit.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()
    fun registerUser(activity: ActivityRegister, userInfo: User) {

        mFireStore.collection(Constants.Users)
            .document(getCurrentUserID())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener {
                Toast.makeText(activity,it.message!!,Toast.LENGTH_SHORT).show()
            }



    }
    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }
    fun getMemberDetails(activity : CreateActivity,email : String){
        mFireStore.collection(Constants.Users)
            .whereEqualTo("email",email)
            .get()
            .addOnSuccessListener {
                    it->
                if(it.documents.size>0){
                    val user= it.documents[0].toObject(User::class.java)
                    activity.foundmember(user!!)
                }else{
                    Toast.makeText(activity,"no user found",Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->

                Log.e(activity.javaClass.simpleName, "Error while getting member.", e)
            }
    }
}