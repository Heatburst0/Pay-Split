package com.example.paysplit.firebase

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.paysplit.ActivityRegister
import com.example.paysplit.CreateActivity
import com.example.paysplit.MainActivity
import com.example.paysplit.fragments.HomeFragment
import com.example.paysplit.fragments.ProfileFragment
import com.example.paysplit.models.PaySplit
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
    fun getMemberDetails(activity: CreateActivity, email: String, function: () -> Unit){
        mFireStore.collection(Constants.Users)
            .orderBy("name")
            .startAt(email)
            .endAt(email + "\uf8ff") // special character to simulate prefix range
            .get()
            .addOnSuccessListener {
                    it->
                if(it.documents.isNotEmpty()){
                    val list = ArrayList<User>()
                    for(i in it.documents){
                        val user = i.toObject(User::class.java)!!
                        list.add(user)
                    }
                    activity.foundmembers(list)
                    function()
                }else{
                    Toast.makeText(activity,"no user found",Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->

                Log.e(activity.javaClass.simpleName, "Error while getting member.", e)
            }
    }
    fun loadUserData(fragement: Fragment) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.Users)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragement.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val loggedInUser = document.toObject(User::class.java)!!
                if(fragement is ProfileFragment)
                    fragement.setUserdata(loggedInUser)

            }.addOnFailureListener {
                (fragement.activity as MainActivity).cancelDialog()
                Toast.makeText(fragement.activity,"Failed",Toast.LENGTH_SHORT).show()
            }
    }
    fun loadUserData(activity: MainActivity) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.Users)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val loggedInUser = document.toObject(User::class.java)!!
                activity.setUserdata(loggedInUser)
            }.addOnFailureListener {
                (activity).cancelDialog()
                Toast.makeText(activity,"Failed",Toast.LENGTH_SHORT).show()
            }
    }
    fun updateUserProfileData(fragement: ProfileFragment, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.Users) // Collection Name
            .document(getCurrentUserID()) // Document ID
            .update(userHashMap) // A hashmap of fields which are to be updated.
            .addOnSuccessListener {
                // Profile data is updated successfully.
                Log.e(fragement.javaClass.simpleName, "Profile Data updated successfully!")

                Toast.makeText(fragement.activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                (fragement.activity as MainActivity).cancelDialog()
                // Notify the success result.

            }
            .addOnFailureListener { e ->
                (fragement.activity as MainActivity).cancelDialog()
                Log.e(
                    fragement.javaClass.simpleName,
                    "Error while updating profile.",
                    e
                )
            }
    }
    fun updateUserProfileData(activity: MainActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.Users) // Collection Name
            .document(getCurrentUserID()) // Document ID
            .update(userHashMap) // A hashmap of fields which are to be updated.
            .addOnSuccessListener {
                // Profile data is updated successfully.
                Log.e(activity.javaClass.simpleName, "Profile Data updated successfully!")
//                Toast.makeText(activity,"FCM Token set",Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e ->

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating profile.",
                    e
                )
            }
    }
    fun addPaySplit(activity : CreateActivity,ps : PaySplit){
        mFireStore.collection(Constants.paysplits)
            .document()
            .set(ps, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "PaySplit created successfully.")
                activity.addPaySplitSuccess()

            }
            .addOnFailureListener {
                Log.e(activity.javaClass.simpleName,it.message.toString())
                Toast.makeText(activity,"Failed to create PaySplit",Toast.LENGTH_SHORT).show()
            }
    }
    fun getPaySplits(fragement: HomeFragment,email : String){
        mFireStore.collection(Constants.paysplits)
            .whereArrayContains(Constants.assignedTo,email)
            .get()
            .addOnSuccessListener {
                Log.e(fragement.javaClass.simpleName, it.documents.toString())
                val list : ArrayList<PaySplit> = ArrayList()
                for(i in it.documents){
                    val ps = i.toObject(PaySplit::class.java)!!
                    ps.id = i.id
                    list.add(ps)
                }
                fragement.setPaySplits(list)


            }.addOnFailureListener {
                Toast.makeText(fragement.activity,"Failed",Toast.LENGTH_SHORT).show()
                Log.e("Error paysplit",it.message.toString())
            }
    }
    fun loadDataHomeFragment(fragement: HomeFragment,upi : Boolean){
        mFireStore.collection(Constants.Users)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener {
                val loggedInUser = it.toObject(User::class.java)!!
                fragement.setUser(loggedInUser,upi)
            }
    }
}