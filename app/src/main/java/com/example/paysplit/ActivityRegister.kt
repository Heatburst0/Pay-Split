package com.example.paysplit

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.paysplit.databinding.ActivityRegisterBinding
import com.example.paysplit.firebase.FirestoreClass
import com.example.paysplit.models.User
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ActivityRegister : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var confirmDialog : Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        binding.btnRegister.setOnClickListener {
            registerUser()
//            showBottomLayout()
        }
    }
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarMyProfileActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = "Register"
        }

        binding.toolbarMyProfileActivity.setNavigationOnClickListener { onBackPressed() }
    }
    private fun registerUser() {
        // Here we get the text from editText and trim the space
        val name: String = binding.etName.text.toString().trim()
        val email: String = binding.etEmail.text.toString().trim()
        val password: String = binding.etPassword.text.toString().trim()
        val upiid : String = binding.etUpiid.text.toString().trim()

        if (validateForm(name, email, password, upiid)) {
            // Show the progress dialog.
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    // If the registration is successfully done
                    if (task.isSuccessful) {
                        auth.currentUser!!.sendEmailVerification().addOnSuccessListener {
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            val registeredEmail = firebaseUser.email!!
                            val user = User(
                                firebaseUser.uid, name, registeredEmail,upiid = upiid
                            )
                            FirestoreClass().registerUser(this@ActivityRegister, user)

                        }

                    } else {
                        Toast.makeText(
                            this@ActivityRegister,
                            task.exception!!.message + "Please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }


    private fun validateForm(name: String, email: String, password: String,upiid : String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                binding.etName.setError("Please enter name.")
                false
            }
            TextUtils.isEmpty(email) -> {
                binding.etEmail.setError("Please enter email.")
                false
            }
            TextUtils.isEmpty(password) -> {
                binding.etPassword.setError("Please enter password.")
                false
            }
            TextUtils.isEmpty(upiid) -> {
                binding.etUpiid.setError("Please enter password.")
                false
            }
            else -> {
                true
            }
        }
    }
    fun userRegisteredSuccess(){
        showBottomLayout()
    }
    private fun showBottomLayout(){
        confirmDialog = BottomSheetDialog(this)
        confirmDialog.setContentView(layoutInflater.inflate(R.layout.confirm_dialog,null))
        confirmDialog.findViewById<ImageView>(R.id.close_btn).setOnClickListener {
            confirmDialog.dismiss()
        }
        confirmDialog.findViewById<Button>(R.id.back_signin).setOnClickListener {
            finish()
        }
        confirmDialog.show()

    }
}