package com.example.paysplit.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paysplit.CreateActivity
import com.example.paysplit.MainActivity
import com.example.paysplit.R
import com.example.paysplit.adapters.PaySplitAdapter
import com.example.paysplit.databinding.FragmentHomeBinding
import com.example.paysplit.firebase.FirestoreClass
import com.example.paysplit.models.PaySplit
import com.example.paysplit.models.User

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private lateinit var loggedinUser : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = FragmentHomeBinding.inflate(layoutInflater)
//        FirestoreClass().loadUserData(this)
//        FirestoreClass().getPaySplits(this,loggedinUser.email)
        FirestoreClass().lodo(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment


        binding.createBtn.setOnClickListener {
            val intent = Intent(activity,CreateActivity::class.java)
            intent.putExtra("user",(activity as MainActivity).loggedinUser)
            startActivity(intent)
        }

        return binding.root
    }
    fun setPaySplits(list : ArrayList<PaySplit>){
        if(list.size>0){
            binding.revPaysplitsHome.visibility = View.VISIBLE
            val adapter = PaySplitAdapter(activity as MainActivity,this,list,loggedinUser)
            binding.revPaysplitsHome.layoutManager = LinearLayoutManager(activity as MainActivity)
            binding.revPaysplitsHome.setHasFixedSize(true)
            binding.revPaysplitsHome.adapter = adapter
        }
    }
    fun setUser(user : User){
        loggedinUser = user
        FirestoreClass().getPaySplits(this,loggedinUser.email)
    }
}