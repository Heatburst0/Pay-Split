package com.example.paysplit.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat.startActivityForResult

object Constants {
    const val Users : String  ="Users"
    const val paysplits : String ="PaySplits"
    const val assignedTo : String = "assignedTo"
    const val apiKey : String ="AAAANLrYtUk:APA91bGbg8eY0XdymqrFOY_eTDTHL7uHynwJAIPqq3lGG_YgZV47y2nbjdDK7Ethe7VRNK3ozfalOprDZI2s85FE14T3N1d1dsMtCp6UbcW07BV6jTemt3EM6BsyxTktHP31b-nojGWu"
    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.

        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

    const val READ_STORAGE_PERMISSION_CODE = 1
    // A unique code of image selection from Phone Storage.
    const val PICK_IMAGE_REQUEST_CODE = 2
}
