package com.example.paysplit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.paysplit.databinding.ActivitySplashBinding
import com.example.paysplit.firebase.FirestoreClass
import com.example.paysplit.models.User
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySplashBinding
    private var auth : FirebaseAuth  = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSignIn.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
        binding.registerBtn.setOnClickListener {
            startActivity(Intent(this,ActivityRegister::class.java))
        }
        binding.btnSignIn.setOnClickListener {
            signInRegisteredUser()
        }

    }
    private fun signInRegisteredUser() {
        // Here we get the text from editText and trim the space
        val username: String = binding.etNameProfile.text.toString().trim { it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

        if (validateForm(username, password)) {
            // Show the progress dialog.

            auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if(auth.currentUser!!.isEmailVerified){
                            Toast.makeText(
                                this@SplashActivity,
                                "Welcome",
                                Toast.LENGTH_LONG
                            ).show()
                            startActivity(Intent(this@SplashActivity,MainActivity::class.java))
                        }else{
                            Toast.makeText(
                                this@SplashActivity,
                                "Please verify your email first",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            this@SplashActivity,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
        }
    }

    private fun validateForm(username: String, password: String): Boolean {
        return if (TextUtils.isEmpty(username)) {
            binding.etNameProfile.setError("Please enter email.")
            false
        } else if (TextUtils.isEmpty(password)) {
            binding.etPassword.setError("Please enter password.")
            false
        } else {
            true
        }
    }

//    fun signInSuccess(user: User) {
//        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
//        this.finish()
//    }
}