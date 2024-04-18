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
import dev.shreyaspatil.easyupipayment.EasyUpiPayment
import dev.shreyaspatil.easyupipayment.listener.PaymentStatusListener
import dev.shreyaspatil.easyupipayment.model.PaymentApp
import dev.shreyaspatil.easyupipayment.model.TransactionDetails

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private lateinit var loggedinUser : User
    private lateinit var payeeUPIid : String
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
            adapter.setOnClickListener(object : PaySplitAdapter.OnClickListener{
                override fun onPayButton(createdby : User, amount: HashMap<String, Double>) {
                    makePayment(createdby.name,createdby.upiid,amount[loggedinUser.id].toString())
                }

            })
        }
    }
    private fun makePayment(name : String,upi : String,amount : String){

        val transcId = System.currentTimeMillis().toString()
        val desc = "Easy UPI payment by Pay Split"

        val easyUpiPayment = EasyUpiPayment(activity as MainActivity) {
            this.paymentApp = PaymentApp.ALL
            this.payeeVpa = upi
            this.payeeName = name
            this.transactionId = transcId
            this.transactionRefId = transcId
            this.payeeMerchantCode = transcId
            this.description = desc
            this.amount = amount
        }
        // END INITIALIZATION

        // Register Listener for Events
        easyUpiPayment.setPaymentStatusListener(object : PaymentStatusListener{
            override fun onTransactionCancelled() {

            }

            override fun onTransactionCompleted(transactionDetails: TransactionDetails) {

            }

        })

        // Start payment / transaction
        easyUpiPayment.startPayment()
    }
    fun setUser(user : User){
        loggedinUser = user
        FirestoreClass().getPaySplits(this,loggedinUser.email)
    }
}