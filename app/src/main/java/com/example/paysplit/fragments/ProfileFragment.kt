package com.example.paysplit.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.paysplit.MainActivity
import com.example.paysplit.R
import com.example.paysplit.databinding.FragmentProfileBinding
import com.example.paysplit.firebase.FirestoreClass
import com.example.paysplit.models.User
import com.example.paysplit.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private lateinit var mUserDetails : User
    private var mSelectedImageFileUri: Uri? = null
    private lateinit var binding : FragmentProfileBinding
    private var dataCollected = false
    private var mProfileImageURL : String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentProfileBinding.inflate(layoutInflater)
        if(!dataCollected) {
            (activity as MainActivity).showProgressDialog("Loading user data")
            FirestoreClass().loadUserData(this)
            dataCollected=true
        }else{
            Glide
                .with(this@ProfileFragment)
                .load(mUserDetails.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(binding.ivProfileUserImage)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding.ivProfileUserImage.setOnClickListener {
            Toast.makeText(activity,"pressed",Toast.LENGTH_SHORT).show()
            if (ContextCompat.checkSelfPermission(activity as MainActivity, Manifest.permission.READ_MEDIA_IMAGES)
                == PackageManager.PERMISSION_GRANTED
            ) {
                val galleryIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(galleryIntent,Constants.PICK_IMAGE_REQUEST_CODE)
            } else {

                ActivityCompat.requestPermissions(
                   this.activity as MainActivity,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }
        binding.btnUpdateProfile.setOnClickListener {
            (activity as MainActivity).showProgressDialog("Updating your profile")
            if(mSelectedImageFileUri!=null){
                uploadImage()
            }else updateUserProfileData()

        }
        return binding.root
    }
    fun setUserdata(user : User){
        (activity as MainActivity).cancelDialog()
        mUserDetails = user
        binding.etNameProfile.setText(user.name)
        binding.etEmailProfile.setText(user.email)
        binding.etUpiid.setText(user.upiid)
        if(user.image.isNotEmpty()){
            Glide
                .with(this@ProfileFragment)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(binding.ivProfileUserImage)
        }
    }

    fun uploadImage(){
        val ext = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType((activity as MainActivity).contentResolver.getType(mSelectedImageFileUri!!))
        Toast.makeText(this.activity,"Success upload",Toast.LENGTH_SHORT).show()
        if(mUserDetails.image.isNotEmpty()){
            val delPrevImage = FirebaseStorage.getInstance().getReferenceFromUrl(mUserDetails.image)
            delPrevImage.delete().addOnSuccessListener {
                Log.i("Firebase storage image","Deleted successfully")
            }
        }
        val storageRef : StorageReference = FirebaseStorage.getInstance().reference.child(
            "user_image"+System.currentTimeMillis()+"."+
            ext
        )
        storageRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
            Log.e(
                "Firebase Image URL",
                it.metadata!!.reference!!.downloadUrl.toString()
            )
            it.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.e("Downloadable Image URL", uri.toString())
                    // assign the image url to the variable.
                    mProfileImageURL = uri.toString()
                    updateUserProfileData()
                }

        }
    }

    private fun updateUserProfileData() {

        val userHashMap = HashMap<String, Any>()
        if (mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image) {
            userHashMap["image"] = mProfileImageURL
        }

        if (binding.etNameProfile.text.toString() != mUserDetails.name) {
            userHashMap["name"] = binding.etNameProfile.text.toString()
        }

        if (binding.etUpiid.text.toString() != mUserDetails.upiid) {

            userHashMap["upiid"] = binding.etUpiid.text.toString()
        }

        // Update the data in the database.
        FirestoreClass().updateUserProfileData(this, userHashMap)
        FirestoreClass().loadUserData(this)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            // The uri of selection image from phone storage.

            mSelectedImageFileUri = data.data!!

            try {
                // Load the user image in the ImageView.
                Glide
                    .with(this)
                    .load(Uri.parse(mSelectedImageFileUri.toString())) // URI of the image
                    .centerCrop() // Scale type of the image.
                    .placeholder(R.drawable.ic_user_place_holder) // A default place holder
                    .into(binding.ivProfileUserImage) // the view in which the image will be loaded.
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val galleryIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(galleryIntent,Constants.PICK_IMAGE_REQUEST_CODE)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    activity,
                    "Oops, you just denied the permission for storage. You can also allow it from settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}