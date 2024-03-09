package com.example.paysplit

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton

import com.example.paysplit.databinding.ActivitySplashBinding
import com.example.paysplit.firebase.FirestoreClass
import com.example.paysplit.models.User
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : BaseActivity() {
    private lateinit var binding : ActivitySplashBinding
    private var auth : FirebaseAuth  = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerBtn.setOnClickListener {
            startActivity(Intent(this,ActivityRegister::class.java))
        }
        binding.btnSignIn.setOnClickListener {
            signInRegisteredUser()
        }
        binding.tvForgotPassword.setOnClickListener {
            openDialog()
        }
        Handler().postDelayed({
            val currentUserID = FirestoreClass().getCurrentUserID()
            // Start the Intro Activity

            if (currentUserID.isNotEmpty()){
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()} // Call this when your activity is done and should be closed.
        },2500)

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
                            signInSuccess()
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

    fun signInSuccess() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        this.finish()
    }
    private fun openDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.forgot_password_view)
        dialog.findViewById<AppCompatButton>(R.id.btn_sendEmailVerify).setOnClickListener {
            val email = dialog.findViewById<EditText>(R.id.et_email_forgot).text.toString()
            if(email.trim().isEmpty()) dialog.findViewById<EditText>(R.id.et_email_forgot).setError("Please enter your E-mail")
            else auth.sendPasswordResetEmail(email).addOnCompleteListener {
                Toast.makeText(this@SplashActivity,"Password reset mail is sent to your email id",Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
        dialog.show()

    }
}