package com.example.paysplit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.paysplit.databinding.ActivityRegisterBinding
import com.example.paysplit.firebase.FirestoreClass
import com.example.paysplit.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ActivityRegister : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarMyProfileActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = "Registere"
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
                            Toast.makeText(
                                this@ActivityRegister,
                                "Email verification is sent to the provided mail, Please verify",
                                Toast.LENGTH_SHORT
                            ).show()
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
        finish()
    }
}