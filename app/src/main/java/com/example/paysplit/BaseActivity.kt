package com.example.paysplit

import android.app.Dialog
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {
    private lateinit var mProgressDialog: Dialog
    fun showProgressDialog(message: String) {
        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.progress_dialog)

        mProgressDialog.findViewById<TextView>(R.id.txt_msg).text = message
        mProgressDialog.setCancelable(false)
        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }
    fun cancelDialog(){
        if(mProgressDialog!=null){
            mProgressDialog.dismiss()
        }
    }
    fun showErrorSnackBar(message: String) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(
                this@BaseActivity,
                R.color.red
            )
        )
        snackBar.show()
    }
}