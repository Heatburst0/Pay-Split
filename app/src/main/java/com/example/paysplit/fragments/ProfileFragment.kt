package com.example.paysplit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.paysplit.MainActivity
import com.example.paysplit.R
import com.example.paysplit.databinding.FragmentProfileBinding
import com.example.paysplit.firebase.FirestoreClass
import com.example.paysplit.models.User

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        (activity as MainActivity).showProgressDialog()
        FirestoreClass().loadUserData(this)
        return binding.root
    }

    companion object {

    }
    fun setUserdata(user : User){
        (activity as MainActivity).cancelDialog()
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
}